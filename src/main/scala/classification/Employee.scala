package classification

/**
  * Created by Hussam on 05.01.19.
  */
// structure of the data as a case class
final case class Employee(age: Int, attrition: String,businessTravel: String,
                          dailyRate: Int, department:String,distanceFromHome:Int,
                          education:Int,educationField:String, employeeCount:Int,
                          employeeNumber:Int,environmentSatisfaction:Int,
                          gender: String,hourlyRate:Int,jobInvolvement: Int,
                          jobLevel:Int, jobRole:String,jobSatisfaction: Int,
                          martialStatus:String,monthlyIncome:Int,monthlyRate:Int,
                          numCompaniesWorkedIn:Int,overTime:String,
                          percentSalaryHike:Int,performanceRating:Int,
                          relationshipSatisfaction:Int,stockOptionLevel:Int,
                          totalWorkingYears:Int,trainingTimesLast:Int,
                          workLifeBalance:Int,yearsAtCompany:Int,
                          yearsInCurrentRole:Int, yearsSinceLastPromotion:Int,
                          yearsWithCurManager:Int
                         )
