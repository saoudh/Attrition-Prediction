package classification


import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}

object MyGUI extends JFXApp {
  println("version:"+util.Properties.versionString)
  val classification= new Classification()


  import scalafx.Includes._


  var btnTrain=new Button{
    text="train"

    onAction=(a:ActionEvent)=>{
  println("train model")
    classification.train()
      println(s"btn-submit called:${a.toString()}")
    }

  }

  var btnPredictTestData=new Button{
    text="predict test data"

    onAction=(a:ActionEvent)=>{
      println("train model")
      classification.predictTestData()
      println(s"btn-submit called:${a.toString()}")
    }

  }

  var btnPredictNewData=new Button{
    text="predict new data"



  }



  var btnSubmit=new Button{
    text="submission"

    onAction=(a:ActionEvent)=>{
      println(s"btn-submit called:${a.toString()}")
    }

  }



  stage = new PrimaryStage {




    val myFeatures:Array[String]=Array("age (i.e. 31)", "businessTravel",
      "dailyRate (102 - 1499)", "distanceFromHome (1 - 29)",

      "environmentSatisfaction (1 - 4)",
      "jobInvolvement (1 - 4)",
      "jobRoleIndex","jobSatisfaction (1 - 4)",
      "monthlyIncome (1009 - 20000)","monthlyRate (2094 - 27000)",
      "numCompaniesWorkedIn (0 - 9)","overTimeIndex (Yes or No)",

      "stockOptionLevel (0 - 3)",
      "totalWorkingYears (0 - 40)","trainingTimesLast (0 - 6)",
      "workLifeBalance (1 - 4)","yearsAtCompany (0 - 40)",
      "yearsInCurrentRole (0 - 18)"
    )
    val newData1=new Employee(18,"No", "Non-Travel", 287,"Research & Development",5, 2,"Life Sciences",1, 1012, 2,"Male",
      73,3, 1,"Research Scientist", 4, "Single", 1051, 13493, 1, "No", 15, 3, 4, 0,0,2,3, 0, 0,0,0
    )
    var myHBoxes: Array[HBox] = Array()
    var myBoxes:Array[Control]=Array()
    for (a <- myFeatures) {

      var label = new Label()
      var myBox:Control=a match
      {
        case "businessTravel"=>new ChoiceBox(ObservableBuffer("Travel_Rarely","Travel_Frequently"))
        case "jobRoleIndex"=>new ChoiceBox(ObservableBuffer("Research Scientist","Laboratory Technician","Manufacturing Director",
          "Healthcare Representative","Manager","Sales Representative","Sales Executive",
          "Research Director","Human Resources"))

        case _ => new TextField()
      }
      label.text = a
      myBoxes:+=myBox

      myHBoxes :+= new HBox {
        children = Seq(label, myBox)
        spacing = 10
        //padding = Insets(10)
      }

    }
    myHBoxes :+=new HBox {
      children = btnTrain
    }
    myHBoxes :+=new HBox {
      children = btnPredictTestData
    }
    myHBoxes :+=new HBox {
      children = btnPredictNewData
    }


    scene = new Scene {
      title = "ButtonBar Demo"



      root=new VBox {

        /*
        myHBoxes+:=new HBox{
         var cb= new ChoiceBox(ObservableBuffer("a","b"))
          cb.value.onChange
          {
          println(cb.value.value.toString())
          }
          children=cb

        }*/


        children=myHBoxes
      }

      btnPredictNewData.onAction=(a:ActionEvent)=>{
        println("train model")

          classification.predictNewData(new Employee(
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
        println(s"btn-submit called:${a.toString()}")
      }
    }
  }

}