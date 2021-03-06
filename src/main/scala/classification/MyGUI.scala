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

  import scalafx.Includes._
  val classification = new Classification()

  var btnTrain = new Button {
    text = "train model"
    // the implicits is needed for the Future-methods
    import scala.concurrent.ExecutionContext.Implicits.global

    // click-listener
    onAction = (a: ActionEvent) => {
      var status = ""
      var text = "training model..."
      // using UI-Thread for setting the status text
      Platform.runLater(new Runnable {
        override def run(): Unit = {
          textarea.setText(text)
        }
      })
      // using different thread for training to prevent freezing of the UI
      val f: Future[String] = Future {
        status = classification.train()
        status
      }
      // after worker-thread is finished, get the notification text and set it to the textarea-element
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
    }
  }


  // text area for displaying the notification and evaluation after training, evaluation and prediction
  val textarea = new TextArea {
  }

  // button for triggering the prediction and evaluation of text data
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

      // process the prediction process of test data in a different thread than the UI-thread
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

  // setting default values to the text fields for prediction of new data
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

  // after button for prediction of new data is triggered,
  // return the text in the text fields
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

  // init. of button for new data
  var btnPredictNewData = new Button {
    text = "predict new data"
  }

  // init. of button for setting default values to text fields
  var btnSetDefaultValues = new Button {
    text = "set default values"
  }


  // main method of scalaFX
  stage = new PrimaryStage {
    // init. of the feature labels
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

    var myHBoxes: Array[HBox] = Array()
    var myBoxes: Array[Control] = Array()
    // assign labels and their corresponding text fields in a loop
    for (labelName <- myFeatures) {

      var label = new Label()
      // the choice boxes have to be initialized seperately, as they contain predefined values
      var myBox: Control = labelName match {
        case "businessTravel" => new ChoiceBox(ObservableBuffer("Travel_Rarely", "Travel_Frequently"))
        case "jobRoleIndex" => new ChoiceBox(ObservableBuffer("Research Scientist", "Laboratory Technician", "Manufacturing Director",
          "Healthcare Representative", "Manager", "Sales Representative", "Sales Executive",
          "Research Director", "Human Resources"))

        case _ => new TextField()
      }
      label.text = labelName
      // add control element (i.e. text field or choice box) to an array to be able to access them individually
      myBoxes :+= myBox

      // add label-textfield pair to an array
      myHBoxes :+= new HBox {
        children = Seq(label, myBox, textarea)
        spacing = 10
        //padding = Insets(10)
      }

    }
    // add all buttons to the window below the text fields
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

    // define the click-listener for setting default values to the text fields
    btnSetDefaultValues.onAction = (a: ActionEvent) => {
      Platform.runLater(new Runnable {
        override def run(): Unit = {
          setDefaultValues(myBoxes = myBoxes)
        }
      })
    }

    // define the click-listener for triggering the prediction of new data process
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
              //the returned array is of the format: [label, [probab. that's true, probab. that's false]]
              // -> label is the first element in the array
              // predicted label of 0.0 means false and 1.0 means true
              val attrition = if (value(0).toString.toDouble == 0.0) "false" else "true"
              // the second element of the array contains two elements:
              // first element is the probablity that label is false and the second element vice versa
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

    // set all elements to the window
    scene = new Scene {
      title = "Attrition Prediction"
      root = new VBox {
        children = myHBoxes
      }


    }
  }
}