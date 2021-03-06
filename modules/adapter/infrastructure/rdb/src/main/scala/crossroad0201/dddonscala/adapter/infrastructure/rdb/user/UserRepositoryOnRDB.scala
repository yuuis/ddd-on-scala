package crossroad0201.dddonscala.adapter.infrastructure.rdb.user

import crossroad0201.dddonscala.domain.{user, UnitOfWork}
import crossroad0201.dddonscala.domain.user.{User, UserRepository}

trait UserRepositoryOnRDB extends UserRepository {

  override def get(id: user.UserId)(implicit uow: UnitOfWork) =
    throw new UnsupportedOperationException("このサンプルでは未実装です。")

  override def save(user: User)(implicit uof: UnitOfWork) = throw new UnsupportedOperationException("このサンプルでは未実装です。")

  override def delete(task: User)(implicit uof: UnitOfWork) = throw new UnsupportedOperationException("このサンプルでは未実装です。")

}
