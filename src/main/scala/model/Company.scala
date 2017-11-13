package model

import com.github.nscala_time.time.Imports._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class Company (idComp: Option[Long], name: String)

class CompanyTable(tag: Tag) extends Table[Company](tag, "company") {
  val idComp = column[Long]("id_comp", O.PrimaryKey)
  val name = column[String]("name")

  //val directorFk = foreignKey("director_id_fk", directorId, TableQuery[StaffTable])(_.id)
  def * = (idComp.?, name) <> (Company.apply _ tupled, Company.unapply)
}
