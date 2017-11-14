package model
import java.time._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class Passenger (idPsg: Option[Int], name: String)

class PassengerTable(tag: Tag) extends Table[Passenger](tag, "passenger") {
  val idPsg = column[Int]("id_psg", O.PrimaryKey)
  val name = column[String]("name")

  def * = (idPsg.?, name) <> (Passenger.apply _ tupled, Passenger.unapply)
}

object PassengerTable {
  val table = TableQuery[PassengerTable]
}

class PassengerRepository(db: Database) {
  val passengerTableQuery = TableQuery[PassengerTable]
  def create(passenger: Passenger): Future[Passenger] = db.run(PassengerTable.table returning PassengerTable.table += passenger)
  def update(passenger: Passenger): Future[Int] = db.run(passengerTableQuery.filter(_.idPsg === passenger.idPsg).update(passenger))
  def delete(idPsg: Int): Future[Int] = db.run(passengerTableQuery.filter(_.idPsg === idPsg).delete)
  def getById(idPsg: Int): Future[Option[Passenger]] = db.run(passengerTableQuery.filter(_.idPsg === idPsg).result.headOption)
}