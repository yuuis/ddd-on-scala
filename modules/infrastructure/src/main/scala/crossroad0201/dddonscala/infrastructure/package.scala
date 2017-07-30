package crossroad0201.dddonscala

import java.util.UUID
import crossroad0201.dddonscala.domain.EntityIdGenerator

object UUIDEntityIdGenerator extends EntityIdGenerator {
  override def genId() = UUID.randomUUID().toString
}
