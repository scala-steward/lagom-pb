package lagompb.readside

import java.sql.Connection

import com.github.ghik.silencer.silent
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.jdbc.{JdbcReadSide, JdbcSession}
import com.typesafe.config.Config
import lagompb.{LagompbEvent, LagompbException}
import lagompb.core.{EventWrapper, MetaData}
import lagompb.util.LagompbProtosCompanions
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext

/**
 * It will be implemented by any jdbc-based readSide Processor.
 * It must be registered in the LagompbApplication
 *
 * @param readSide the jdbc readSide component
 * @param session  the JDBC session
 * @param config   the configuration instance
 * @param ec       the scala concurrency execution context
 * @tparam TState the aggregate state type
 */
@silent abstract class LagompbJdbcRead[TState <: scalapb.GeneratedMessage](
    readSide: JdbcReadSide,
    session: JdbcSession,
    config: Config
)(implicit ec: ExecutionContext)
    extends ReadSideProcessor[LagompbEvent] {

  val log: Logger =
    LoggerFactory.getLogger(getClass)

  final override def buildHandler(): ReadSideProcessor.ReadSideHandler[LagompbEvent] =
    readSide
      .builder[LagompbEvent](readSideId)
      .setEventHandler(handleEvent)
      .build()

  private def handleEvent: (Connection, EventStreamElement[EventWrapper]) => Unit = {
    (connection: Connection, eventElement: EventStreamElement[EventWrapper]) =>
      {
        val eventWrapper: EventWrapper = eventElement.event
        LagompbProtosCompanions
          .getCompanion(eventWrapper.getEvent)
          .fold[Unit](throw new LagompbException(s"[Lagompb] unable to parse event ${eventWrapper.getEvent.typeUrl}"))(
            comp => {
              // pass to the user defined event jdbc readSide event handler
              handle(
                connection,
                eventWrapper.getEvent.unpack(comp),
                eventWrapper.getResultingState
                  .unpack[TState](aggregateStateCompanion),
                eventWrapper.getMeta
              )
            }
          )
      }
  }

  //  An identifier for this read side. This will be used to store offsets in the offset store.
  final def readSideId: String = config.getString("lagompb.service-name")

  final override def aggregateTags: Set[AggregateEventTag[LagompbEvent]] =
    LagompbEvent.Tag.allTags

  /**
   * Handles aggregate event persisted and made available for read model
   *
   * @param event the aggregate event
   * @param state the Lagompb state that wraps the actual state and some meta data
   */
  def handle(connection: Connection, event: scalapb.GeneratedMessage, state: TState, metaData: MetaData): Unit

  /**
   * aggregate state. it is a generated scalapb message extending the LagompbState trait
   *
   * @return aggregate state
   */
  def aggregateStateCompanion: scalapb.GeneratedMessageCompanion[TState]
}
