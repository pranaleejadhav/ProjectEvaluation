//
//  ScoreBoardViewController.swift
//  ProjectEvaluation
//
//  Created by Pranalee Jadhav on 11/30/18.
//  Copyright © 2018 Pranalee Jadhav. All rights reserved.
//

///


import UIKit
import SVProgressHUD

class customTableViewCell: UITableViewCell {
    
    @IBOutlet weak var teamName: UILabel!
    @IBOutlet weak var scores: UILabel!
    @IBOutlet weak var myscore: UILabel!
    @IBOutlet weak var evaluationsCnt: UILabel!
    
    
}

class ScoreBoardViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    var tableArray2 = [Dictionary<String, Any>]()
    var originalArr = [Dictionary<String, Any>]()
     var deviceType = true
    
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "Score Board"
        navigationController?.navigationBar.barTintColor = #colorLiteral(red: 0.9203050733, green: 0.3588146567, blue: 0.3351347446, alpha: 1)
        navigationController?.navigationBar.tintColor = UIColor.white
        let textAttributes = [NSAttributedString.Key.foregroundColor:UIColor.white]
        navigationController?.navigationBar.titleTextAttributes = textAttributes

        tableView.separatorColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
        tableView.dataSource = self
        tableView.delegate = self
        tableView.tableFooterView = UIView()
        
        if UIDevice.current.userInterfaceIdiom == .pad {
           
            deviceType = false
        }
        
        getData()
        // Do any additional setup after loading the view.
    }

    func getData() {
        //show loader
        SVProgressHUD.show()
        let userid = UserDefaults.standard.string(forKey: "userid") ?? ""
        getAPIRequest(server_api: "userteams?userId=\(userid)", handler: {(data) in
            //dismiss loader
            SVProgressHUD.dismiss()
            if let val = data["code"] as? Int{
                switch(val){
                case 0: self.showMsg(title: "Oops!", subTitle: "No Internet")
                    break
                    
                default:
                    self.showMsg(title: "Error", subTitle: "Please try again")
                }
            } else{
                self.tableArray2 = data["teams"] as! [Dictionary<String, Any>]
                
                getAPIRequest(server_api: "teams", handler: {(data) in
                    //dismiss loader
                    SVProgressHUD.dismiss()
                    if let val = data["code"] as? Int{
                        switch(val){
                        case 0: self.showMsg(title: "Oops!", subTitle: "No Internet")
                            break
                            
                        default:
                            self.showMsg(title: "Error", subTitle: "Please try again")
                        }
                    } else{
                        let arr = data["data"] as! [Dictionary<String, Any>]
                        
                       arr.forEach{ ( item:Dictionary<String, Any>) in
                            let str = item["teamId"] as? String
                            var i = true
                            for item2 in self.tableArray2 {
                                if (item2["teamId"] as? String) == str {
                                    var temp = item
                                    if let score = item2["score"] as? Dictionary<String,String>{
                                       //temp["_id"] =
                                        temp["myscore"] = score["$numberDecimal"] ?? ""
                                    }
                                    
                                    
                                    self.originalArr.append(temp)
                                    i = false
                                    break;
                                }
                            }
                            if i {
                                self.originalArr.append(item)
                            }
                            
                        }
                        print(self.originalArr)
                        self.tableView.reloadData()
                    }
                })
                
                
                
            }
        })
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return originalArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellItem") as!  customTableViewCell  //1.
        cell.teamName.text = originalArr[indexPath.row]["teamId"] as? String
        if let score = originalArr[indexPath.row]["score"] as? Dictionary<String,String>{
            cell.scores.text = "Average Score: " + (score["$numberDecimal"] ?? "")
        }
        if let myscore = (originalArr[indexPath.row]["myscore"] as? String) {
            cell.myscore.text = "My Score: \(myscore)"
        }
        
        cell.evaluationsCnt.text = "Evaluations Count: " + String((originalArr[indexPath.row]["evaluationscount"] as? Int) ?? 0)
        return cell
        
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if deviceType {
            return 80.0
        } else {
            return 100.0
        }
        
    }

    
    //show alertbox
    func showMsg(title: String, subTitle: String) -> Void {
        DispatchQueue.main.async(execute: {
            let alertController = UIAlertController(title: title, message:
                subTitle, preferredStyle: UIAlertController.Style.alert)
            alertController.addAction(UIAlertAction(title: "Okay", style: UIAlertAction.Style.default,handler: nil))
            self.present(alertController, animated: true, completion: nil)
        })
    }

}
