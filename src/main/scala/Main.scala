import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import java.time._
import java.time.format.DateTimeFormatter


object Main {
  val db = Database.forConfig("airport")

  val tripRepository = new TripRepository(db)
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)
  val passInTripRepository = new PassInTripRepository(db)

  def main(args: Array[String]): Unit ={
    init()
    databaseFill()
  }

  def init(): Unit = {
//    Await.result(db.run(TripTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
//    Await.result(db.run(PassengerTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
//    Await.result(db.run(CompanyTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
//    Await.result(db.run(PassInTripTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
//    Await.result(db.run(PassInTripToPassengerTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
//    Await.result(db.run(PassInTripToTripTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
//    Await.result(db.run(TripToCompanyTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
  }


  def databaseFill(): Unit = {
    for(company <- listCompanies){
      Await.result(companyRepository.create(Company(Some(company._1), company._2)), scala.concurrent.duration.Duration.Inf)
    }

    for(passenger <- listPassengers){
      Await.result(passengerRepository.create(Passenger(Some(passenger._1), passenger._2)), scala.concurrent.duration.Duration.Inf)
    }

    for(trip <- listTrips){
      Await.result(tripRepository.create(Trip(Some(trip._1), trip._2, trip._3, trip._4, trip._5,
        LocalDateTime.parse(converter(trip._6), pattern), LocalDateTime.parse(converter(trip._7), pattern))),
        scala.concurrent.duration.Duration.Inf)
    }

    for(pass <- listPassInTrip){
      Await.result(passInTripRepository.create(PassInTrip(Some(pass._1), LocalDateTime.parse(converter(pass._2), pattern)
        , pass._3, pass._4)), scala.concurrent.duration.Duration.Inf)
    }
  }
}