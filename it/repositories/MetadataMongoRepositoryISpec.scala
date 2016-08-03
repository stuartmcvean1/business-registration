import java.util.UUID

import models.Metadata
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import repositories.MetadataMongoRepository
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.mongo.MongoSpecSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MetadataMongoRepositoryISpec extends UnitSpec with MongoSpecSupport with BeforeAndAfterEach with ScalaFutures with Eventually with WithFakeApplication {

  class Setup {
    val repository = new MetadataMongoRepository()
    //await(repository.drop)
    await(repository.ensureIndexes)
  }


  "MetadataRepository" should {

    "be able to retrieve a document that has been created by OID" in new Setup {

      val randomOid = UUID.randomUUID().toString
      val randomRegid = UUID.randomUUID().toString

      val metadata = Metadata.empty.copy(OID = randomOid, registrationID = randomRegid)

      val metdataResponse = await(repository.createMetadata(metadata))

      metdataResponse.OID shouldBe (randomOid)

      val mdByOid = await(repository.searchMetadata(randomOid))

      mdByOid shouldBe(defined)
      mdByOid.get.OID shouldBe (randomOid)
      mdByOid.get.registrationID shouldBe (randomRegid)
    }

    "be able to retrieve a document that has been created by registration id" in new Setup {

      val randomOid = UUID.randomUUID().toString
      val randomRegid = UUID.randomUUID().toString

      val metadata = Metadata.empty.copy(OID = randomOid, registrationID = randomRegid)

      val metdataResponse = await(repository.createMetadata(metadata))

      metdataResponse.registrationID shouldBe (randomRegid)

      val mdByRegId = await(repository.retrieveMetadata(randomRegid))

      mdByRegId shouldBe(defined)
      mdByRegId.get.OID shouldBe (randomOid)
      mdByRegId.get.registrationID shouldBe (randomRegid)
    }

  }
}