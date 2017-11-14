package model

import java.time._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class Company (idComp: Option[Int], name: String)

class CompanyTable(tag: Tag) extends Table[Company](tag, "company") {
  val idComp = column[Int]("id_comp", O.PrimaryKey)
  val name = column[String]("name")

  def * = (idComp.?, name) <> (Company.apply _ tupled, Company.unapply)
}

object CompanyTable {
  val table = TableQuery[CompanyTable]
}

class CompanyRepository(db: Database) {
  val companyTableQuery = TableQuery[CompanyTable]
  def create(company: Company): Future[Company] = db.run(CompanyTable.table returning CompanyTable.table
    += company)
  def update(company: Company): Future[Int] = db.run(companyTableQuery.filter(_.idComp === company.idComp).update(company))
  def delete(idComp: Int): Future[Int] = db.run(companyTableQuery.filter(_.idComp === idComp).delete)
  def getById(idComp: Int): Future[Option[Company]] = db.run(companyTableQuery.filter(_.idComp === idComp).result.headOption)
}