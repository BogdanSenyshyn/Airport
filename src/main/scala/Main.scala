import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDateTime


object Main {
  val db = Database.forConfig("airport")

  val tripRepository = new TripRepository(db)
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)
  val passInTripRepository = new PassInTripRepository(db)

  def main(args: Array[String]): Unit ={
    //init()
    databaseFill()
  }

  def init(): Unit = {
    Await.result(db.run(CompanyTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
    Await.result(db.run(PassengerTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
    Await.result(db.run(TripTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
    Await.result(db.run(PassInTripTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
    Await.result(db.run(PassInTripToPassengerTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
    Await.result(db.run(PassInTripToTripTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
    Await.result(db.run(TripToCompanyTable.table.schema.create), scala.concurrent.duration.Duration.Inf)
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

    //67
    val sixtySevenQuery = TripTable.table.groupBy( t => (t.townFrom, t.townTo)).
      map{case ((from, to), counter) => (counter.length)}.sortBy(_.desc).take(1).result.statements.mkString

    //68 almost
    val subquery1 = TripTable.table.filter(t => t.townFrom >= t.townTo)
      .groupBy(t => (t.townFrom, t.townTo))
      .map{ case ((townFrom, townTo), group) => (group.length, townFrom, townTo)}

    val subquery2 = TripTable.table.filter(t => t.townTo > t.townFrom)
      .groupBy(t => (t.townFrom, t.townTo))
      .map{ case ((townFrom, townTo), group) => (group.length, townFrom, townTo)}

    val t = subquery1 union subquery2
    val sixtyEightQuery = t.map { case subquery1 => (subquery1._1, subquery1._2, subquery1._3) }
      .groupBy { case (sub1, sub2, sub3) => (sub2, sub3) }
      .map { case ((sub2, sub3), group) => (group.map(_._1).sum, sub2, sub3) }
      .sortBy(_._3.desc)
      .take(1).length.result.statements.mkString

    //63
    val sixtyThree =
      PassengerTable.table.join(PassInTripTable.table).on(_.idPsg === _.idPsg).
        groupBy{ case(name, place) => (place.place, name.name)}.
        map{case(name, places) => (name, places.map(_._2.place).length)}.
        filter{ case(name, places) => places > 1}.
        map(_._1._2).result.statements.mkString

    //72
    val passengerJoinPassInTrip = for {
      (psg, psgintrip) <- PassengerTable.table join PassInTripTable.table on(_.idPsg === _.idPsg)
    } yield (psg.name, psgintrip.tripNo)

    val seventyTwoQuery = passengerJoinPassInTrip.
      groupBy(_._1).
      map{ case(name, tripNo) => name -> tripNo.length }.
      sortBy(_._2.desc).result.statements.mkString

    //77
    val seventySevenQuery = PassInTripTable.table.join(TripTable.table).on(_.tripNo === _.tripNo)
      .filter(t => t._2.townFrom === "Rostov")
      .groupBy(t => (t._1.tripNo, t._1.date))
      .map{case ((trip, date), count) => (count.countDistinct, date)}
      .sortBy(_._2.asc)
      .result.statements.mkString

    //102
    val subquery5 = (for {
      pit <- PassInTripTable.table
      t <- TripTable.table if pit.tripNo === t.tripNo
    } yield (pit, t)).
      map{ case(pit, t) => (pit, Case If(t.townFrom <= t.townTo) Then t.townFrom ++ t.townTo Else t.townTo ++ t.townFrom)}.
      groupBy(_._1.idPsg).
      map{case(pit, count) => (pit, count.map(_._2).countDistinct)}.
      filter(p => p._2 === 1).map(_._1)

    val hundredTwoQuery = PassengerTable.table.filter(_.idPsg in subquery5).map(_.name).result.statements.mkString

    //103
    val hundredThreeQuery = (for {
      t <- TripTable.table
      tt <- TripTable.table
      ttt <- TripTable.table
    }yield(t.tripNo, tt.tripNo, ttt.tripNo)).filter(t => t._2 > t._1 && t._3 > t._2).groupBy{_ => true}.
      map{case (_, group) => (group.map(_._1).min, group.map(_._2).min, group.map(_._3).min,
        group.map(_._1).max, group.map(_._2).max, group.map(_._3).max)}.result.statements.mkString

    //66
    val sixtySixQuery = {
      PassInTripTable.table.join(TripTable.table).on(_.tripNo === _.tripNo)
        .filter(_._2.townFrom === "Rostov")
        .filter(_._1.date < LocalDateTime.parse("2003-04-07T00:00"))
        .groupBy(t => (t._1.date,t._2.townFrom))
        .map{case((date,town),count) => (count.countDistinct,date)}
        .result.statements.mkString
    }

    //88
    val eightyEightTask= {
      PassengerTable.table.join(PassInTripTable.table).on(_.idPsg === _.idPsg)
        .join(TripTable.table).on(_._2.tripNo === _.tripNo)
        .join(CompanyTable.table).on(_._2.idComp === _.idComp)
        .groupBy(t => (t._1._1._1.name,t._2.name))
        .map{case((name,comp),count) => (name,comp,count.length)}
        .sortBy(_._3.desc).take(2)
        .result.statements.mkString
    }

    //114
    val oneHundredFourteenQuery = {
      PassengerTable.table.join(PassInTripTable.table).on(_.idPsg === _.idPsg)
        .join(TripTable.table).on(_._2.tripNo === _.tripNo)
        .groupBy(t => (t._1._1.name,t._1._2.place))
        .map{case((name,place),count) => (name, count.length)}
        .sortBy(_._2.desc).take(3)
        .result.statements.mkString
    }

    //87
    val eightySevenQuery = {
      PassengerTable.table.join(PassInTripTable.table).on(_.idPsg === _.idPsg)
        .join(TripTable.table).on(_._2.tripNo === _.tripNo)
        .filter(t => t._2.townTo === "Moscow")
        .groupBy(t => (t._1._1.name, t._2.townTo,t._2.townFrom))
        .map{case((name,to,from),count) => (name, count.length)}
        .filter{ case(name, count) => count > 1}
        .result.statements.mkString
    }

    //94
    val ninetyFourQuery = {
      PassInTripTable.table.join(TripTable.table).on(_.tripNo === _.tripNo)
        .filter(_._2.townFrom === "Rostov")
        .filter(_._1.date < LocalDateTime.parse("2003-04-07T00:00"))
        .groupBy(t => (t._1.date,t._2.townFrom))
        .map{case((date,town),count) => (count.countDistinct,date)}
        .result.statements.mkString
    }

    //107
    val oneHundredSeventhTask = {
      PassInTripTable.table.join(TripTable.table).on(_.tripNo === _.tripNo)
        .join(CompanyTable.table).on(_._2.idComp === _.idComp)
        .filter(_._1._2.townFrom === "Rostov")
        .filter(_._1._1.date < LocalDateTime.parse("2003-04-30T00:00"))
        .map{t => (t._1._1.idPsg,t._2.name,t._1._2.tripNo,t._1._1.date)}.sortBy(t => t._4)
        .filter(t => t._1 === 6).sortBy(_._4.desc).take(1)
        .map{case (psgId,company,trip,date) => (company,trip,date)}
        .result.statements.mkString
    }

    //95 (1, 4)
    val tripJoinCompany = for {
      (trip, comp) <- TripTable.table join CompanyTable.table on(_.idComp === _.idComp)
    } yield (trip.tripNo, comp.idComp, comp.name)

    val tripsAndPassInTrip = for {
      (tripJComp, passInTrips) <- tripJoinCompany join PassInTripTable.table on(_._1 === _.tripNo)
    } yield (tripJComp._3, passInTrips.tripNo)

    val tripsAndPassengers = for {
      (passInTrip, tripJComp) <- PassInTripTable.table join tripJoinCompany on(_.tripNo === _._1)
    } yield (tripJComp._1, tripJComp._3)
    //
    val q95_1 = tripsAndPassInTrip.
      groupBy(_._1).
      map{ case(name, tripNo) => name -> tripNo.length }

    val q95_5 = tripsAndPassengers.
      groupBy(_._2).
      map{ case(name, tripNo) => name -> tripNo.length}
    println(q95_1.result.statements.mkString)

    //Queries almost working
    //110 (Parsing of strings not working)(better to comment it for not interuppting)

    val oneHundredTenTask = {
      PassengerTable.table.join(PassInTripTable.table).on(_.idPsg === _.idPsg)
        .join(TripTable.table).on(_._2.tripNo === _.tripNo)
        .filter(t => t._2.timeIn < t._2.timeOut && LocalDateTime.parse((t._2.timeOut.toString)).getDayOfWeek.name.equals("SATURDAY")
          && LocalDateTime.parse((t._2.timeIn.toString)).getDayOfWeek.name.equals("SUNDAY"))
        .groupBy(t => (t._1._1.name,t._2.timeOut))
        .map{case(name) => (name._1)}
        .result.statements.mkString
    }
  }
}