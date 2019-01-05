package classification


import org.apache.spark.ml.linalg.DenseVector
import javafx.application.Platform
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MyGUI extends JFXApp {
  println("version:" + util.Properties.versionString)
  val classification = new Classification()


  import scalafx.Includes._


  var btnTrain = new Button {
    text = "train model"

    import scala.concurrent.ExecutionContext.Implicits.global

    onAction = (a: ActionEvent) => {
      var status = ""
      println("training model")
      var text = "training model..."
      Platform.runLater(new Runnable {
        override def run(): Unit = {
          textarea.setText(text)
        }
      })

      val f: Future[String] = Future {
        status = classification.train()
        status
      }

      f.onComplete {
        case Success(value) =>
          Platform.runLater(new Runnable {
            override def run(): Unit = {
              text += "\n" + value
              textarea.setText(text)
            }
          })
        case Failure(e) =>
          Platform.runLater(new Runnable {
            override def run(): Unit = {
              text += "\n" + "Training error!"
              textarea.setText(text)
            }
          })
      }

      println(s"btn-submit called:${status}")

      println("trained model")

    }

  }


  val textarea = new TextArea {

  }

  var btnPredictTestData = new Button {
    text = "predict test data"

    import scala.concurrent.ExecutionContext.Implicits.global


    onAction = (a: ActionEvent) => {
      var status = ""
      var text = "Predict test data..."
      Platform.runLater(new Runnable {
        override def run(): Unit = {
          textarea.setText(text)
        }
      })

      val f: Future[String] = Future {
        status = classification.predictTestData()
        status
      }

      f.onComplete {
        case Success(value) =>
          Platform.runLater(new Runnable {
            override def run(): Unit = {
              text += "\n" + value
              textarea.setText(text)
            }
          })
        case Failure(e) =>
          Platform.runLater(new Runnable {
            override def run(): Unit = {
              text += "\n" + "Prediction error!"
              textarea.setText(text)
            }
          })
      }


    }
  }

  def setDefaultValues(myBoxes: Array[Control]): Unit = {
    myBoxes(0).asInstanceOf[TextField].text.value = "31"
    myBoxes(1).asInstanceOf[ChoiceBox[String]].value = "Travel_Rarely"
    myBoxes(2).asInstanceOf[TextField].text.value = "102"
    myBoxes(3).asInstanceOf[TextField].text.value = "3"
    myBoxes(4).asInstanceOf[TextField].text.value = "2"
    myBoxes(5).asInstanceOf[TextField].text.value = "2"
    myBoxes(6).asInstanceOf[ChoiceBox[String]].value = "Research Scientist"
    myBoxes(7).asInstanceOf[TextField].text.value = "3"
    myBoxes(8).asInstanceOf[TextField].text.value = "3500"
    myBoxes(9).asInstanceOf[TextField].text.value = "5000"
    myBoxes(10).asInstanceOf[TextField].text.value = "4"
    myBoxes(11).asInstanceOf[TextField].text.value = "Yes"
    myBoxes(12).asInstanceOf[TextField].text.value = "1"
    myBoxes(13).asInstanceOf[TextField].text.value = "3"
    myBoxes(14).asInstanceOf[TextField].text.value = "2"
    myBoxes(15).asInstanceOf[TextField].text.value = "2"
    myBoxes(16).asInstanceOf[TextField].text.value = "4"
    myBoxes(17).asInstanceOf[TextField].text.value = "2"

  }

  def predictNewData(myBoxes: Array[Control]): Seq[Any] = {
    val myPrediction: Seq[Any] = classification.predictNewData(new Employee(
      myBoxes(0).asInstanceOf[TextField].text.value.toInt,
      "Yes",
      myBoxes(1).asInstanceOf[ChoiceBox[String]].value.value.toString(),
      myBoxes(2).asInstanceOf[TextField].text.value.toInt,
      "Sales",
      myBoxes(3).asInstanceOf[TextField].text.value.toInt,
      1,
      "Other",
      1,
      1,
      myBoxes(4).asInstanceOf[TextField].text.value.toInt,
      "Male",
      1,
      myBoxes(5).asInstanceOf[TextField].text.value.toInt,
      0,
      myBoxes(6).asInstanceOf[ChoiceBox[String]].value.value.toString(),
      myBoxes(7).asInstanceOf[TextField].text.value.toInt,
      "Single",
      myBoxes(8).asInstanceOf[TextField].text.value.toInt,
      myBoxes(9).asInstanceOf[TextField].text.value.toInt,
      myBoxes(10).asInstanceOf[TextField].text.value.toInt,
      myBoxes(11).asInstanceOf[TextField].text.value,
      11,
      3,
      1,
      myBoxes(12).asInstanceOf[TextField].text.value.toInt,
      myBoxes(13).asInstanceOf[TextField].text.value.toInt,
      myBoxes(14).asInstanceOf[TextField].text.value.toInt,
      myBoxes(15).asInstanceOf[TextField].text.value.toInt,
      myBoxes(16).asInstanceOf[TextField].text.value.toInt,
      myBoxes(17).asInstanceOf[TextField].text.value.toInt,
      0,
      0

    )
    )
    myPrediction
  }

  var btnPredictNewData = new Button {
    text = "predict new data"


  }


  var btnSetDefaultValues = new Button {
    text = "set default values"
  }


  stage = new PrimaryStage {


    val myFeatures: Array[String] = Array("age (i.e. 31)", "businessTravel",
      "dailyRate (102 - 1499)", "distanceFromHome (1 - 29)",

      "environmentSatisfaction (1 - 4)",
      "jobInvolvement (1 - 4)",
      "jobRoleIndex", "jobSatisfaction (1 - 4)",
      "monthlyIncome (1009 - 20000)", "monthlyRate (2094 - 27000)",
      "numCompaniesWorkedIn (0 - 9)", "overTimeIndex (Yes or No)",

      "stockOptionLevel (0 - 3)",
      "totalWorkingYears (0 - 40)", "trainingTimesLast (0 - 6)",
      "workLifeBalance (1 - 4)", "yearsAtCompany (0 - 40)",
      "yearsInCurrentRole (0 - 18)"
    )
    val newData1 = new Employee(18, "No", "Non-Travel", 287, "Research & Development", 5, 2, "Life Sciences", 1, 1012, 2, "Male",
      73, 3, 1, "Research Scientist", 4, "Single", 1051, 13493, 1, "No", 15, 3, 4, 0, 0, 2, 3, 0, 0, 0, 0
    )
    var myHBoxes: Array[HBox] = Array()
    var myBoxes: Array[Control] = Array()
    for (a <- myFeatures) {

      var label = new Label()
      var myBox: Control = a match {
        case "businessTravel" => new ChoiceBox(ObservableBuffer("Travel_Rarely", "Travel_Frequently"))
        case "jobRoleIndex" => new ChoiceBox(ObservableBuffer("Research Scientist", "Laboratory Technician", "Manufacturing Director",
          "Healthcare Representative", "Manager", "Sales Representative", "Sales Executive",
          "Research Director", "Human Resources"))

        case _ => new TextField()
      }
      label.text = a
      myBoxes :+= myBox

      myHBoxes :+= new HBox {
        children = Seq(label, myBox, textarea)
        spacing = 10
        //padding = Insets(10)
      }

    }
    myHBoxes :+= new HBox {
      children = btnSetDefaultValues
    }
    myHBoxes :+= new HBox {
      children = btnTrain
    }
    myHBoxes :+= new HBox {
      children = btnPredictTestData
    }
    myHBoxes :+= new HBox {
      children = btnPredictNewData
    }


    scene = new Scene {
      title = "Attrition Precition"


      root = new VBox {

        children = myHBoxes
      }

      btnSetDefaultValues.onAction = (a: ActionEvent) => {
        println("setdefaultvalues1")

        Platform.runLater(new Runnable {
          override def run(): Unit = {
            println("setdefaultvalues2")
            setDefaultValues(myBoxes = myBoxes)
          }
        })
      }

      btnPredictNewData.onAction = (a: ActionEvent) => {
        import scala.concurrent.ExecutionContext.Implicits.global

        var report: Seq[Any] = null
        var text = "Starting prediction..."
        val f: Future[Seq[Any]] = Future {
          report = predictNewData(myBoxes)
          report
        }

        f.onComplete {
          case Success(value) =>
            Platform.runLater(new Runnable {
              override def run(): Unit = {
                val attrition = if (value(0).toString.toDouble == 0.0) "false" else "true"
                val indexOfPercentage = if (value(0).toString.toDouble == 0.0) 0 else 1
                val probability = value(1).asInstanceOf[DenseVector](indexOfPercentage).toString.toDouble
                textarea.setText("prediction: \nAttrition is " + attrition + "\nwith probability: " + ((probability * 100).round / 100.toDouble) * 100 + " %")
              }
            })
          case Failure(e) => println("error:" + e)
            Platform.runLater(new Runnable {
              override def run(): Unit = {
                text = "Prediction error!"
                textarea.setText(text)
              }
            })
        }


      }

    }
  }


}