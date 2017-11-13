import slick.jdbc.PostgresProfile.api._
import com.github.nscala_time.time.Imports._
package object model {
  implicit val dataTimeLongMapper = MappedColumnType.base[DateTime, String](_.toString, _)
}