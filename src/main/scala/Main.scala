import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  val db = Database.forConfig("airport")

  val tripRepository = new TripRepository(db)
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)
  val passInTripRepository = new PassInTripRepository(db)


  def main(args: Array[String]): Unit ={
    init()
  }

  def init(): Unit = {
//    Await.result(db.run(CountryTable.table.schema.create), Duration.Inf)
//    Await.result(db.run(StaffTable.table.schema.create), Duration.Inf)
//    Await.result(db.run(GenreTable.table.schema.create), Duration.Inf)
//    Await.result(db.run(FilmTable.table.schema.create), Duration.Inf)
//    Await.result(db.run(FilmToGenreTable.table.schema.create), Duration.Inf)
//    Await.result(db.run(FilmToCastTable.table.schema.create), Duration.Inf)
//    Await.result(db.run(FilmToCountryTable.table.schema.create), Duration.Inf)
  }
}