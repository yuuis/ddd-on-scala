package crossroad0201.dddonscala.adapter.sample

import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.domain.{EntityIdGenerator, UnitOfWork}
import crossroad0201.dddonscala.domain.task.{
  Task,
  TaskClosed,
  TaskCreated,
  TaskEventPublisher,
  TaskId,
  TaskName,
  TaskRepository
}
import crossroad0201.dddonscala.domain.user.UserId
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Success

class SampleAdapterSpec extends FeatureSpec with GivenWhenThen with Matchers with MockFactory {

  feature("タスクを新規作成できる") {
    scenario("タスク名と作成者を指定して、タスクを新規作成する") {
      new WithFixture {
        Then("タスクが保存される")
        (mockTaskRepository
          .save(_: Task)(_: UnitOfWork))
          .expects(where {
            case (aTask, _) =>
              true
          })
          .onCall { (aTask, _) =>
            Success(aTask)
          }
          .once

        Then("タスク作成イベントが発行される")
        (mockTaskEventPublisher
          .publish(_: TaskCreated)(_: UnitOfWork))
          .expects(where {
            case (aEvent, _) =>
              true
          })
          .onCall { (aEvent, _) =>
            Success(aEvent)
          }
          .once

        When("タスクを作成する")
        val actual = sut.createTask("テストタスク")

        Then("作成されたタスクのIDが返される")
        actual should contain("1")
      }
    }
  }

  feature("タスクをクローズできる") {
    scenario("存在するタスクを指定してクローズする") {
      new WithFixture {
        Given("タスクが存在する")
        (mockTaskRepository
          .get(_: TaskId)(_: UnitOfWork))
          .expects(TaskId("123"), *)
          .onCall { (aId, _) =>
            Success(Some {
              Task(
                id       = aId,
                name     = TaskName("テストタスク"),
                authorId = UserId("USER001")
              )
            })
          }
          .once

        Then("タスクが保存される")
        (mockTaskRepository
          .save(_: Task)(_: UnitOfWork))
          .expects(where {
            case (aTask, _) =>
              true
          })
          .onCall { (aTask, _) =>
            Success(aTask)
          }
          .once

        Then("タスククローズイベントが発行される")
        (mockTaskEventPublisher
          .publish(_: TaskClosed)(_: UnitOfWork))
          .expects(where {
            case (aEvent, _) =>
              true
          })
          .onCall { (aEvent, _) =>
            Success(aEvent)
          }
          .once

        When("タスクをクローズする")
        val actual = sut.closeTask("123")

        Then("クローズされたタスクのIDが返される")
        actual should contain("123")
      }
    }
  }

  trait WithFixture {
    val mockTaskRepository     = mock[TaskRepository]
    val mockTaskEventPublisher = mock[TaskEventPublisher]

    val sut = new SampleAdapter {
      override val taskService = new TaskService {
        override implicit val entityIdGenerator = new EntityIdGenerator {
          var currentId: Int = 0
          override def genId() = {
            currentId = 1
            currentId.toString
          }
        }
        override val taskRepository     = mockTaskRepository
        override val taskEventPublisher = mockTaskEventPublisher

        override def tx[A](f:         (UnitOfWork) => A) = f(new UnitOfWork {})
        override def txReadonly[A](f: (UnitOfWork) => A) = f(new UnitOfWork {})
      }
    }
  }
}