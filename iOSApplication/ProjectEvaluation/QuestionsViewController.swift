//
//  QuestionsViewController.swift
//  ProjectEvaluation
//
//  Created by Pranalee Jadhav on 12/8/18.
//  Copyright © 2018 Pranalee Jadhav. All rights reserved.
//

import UIKit
import SVProgressHUD

class QuestionsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var tableView: UITableView!
    var questionList:[Dictionary<String,Any>] = [Dictionary<String,Any>]()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "Survey Questions"
        navigationController?.navigationBar.barTintColor = #colorLiteral(red: 0.9203050733, green: 0.3588146567, blue: 0.3351347446, alpha: 1)
        navigationController?.navigationBar.tintColor = UIColor.white
        let textAttributes = [NSAttributedString.Key.foregroundColor:UIColor.white]
        navigationController?.navigationBar.titleTextAttributes = textAttributes
        
        tableView.separatorColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
        tableView.dataSource = self
        tableView.delegate = self
        tableView.tableFooterView = UIView()
        tableView.rowHeight = UITableView.automaticDimension
        
        if UIDevice.current.userInterfaceIdiom == .pad {
            
            tableView.estimatedRowHeight = 80
        } else {
            tableView.estimatedRowHeight = 60
        }
        getData()

    }
    
    func getData() {
        //show loader
        SVProgressHUD.show()
        getAPIRequest(server_api: "questions", handler: {(data) in
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
                self.questionList = data["data"] as! [Dictionary<String, Any>]
                self.tableView.reloadData()
            }
        })
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return questionList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellItem")  //1.
        cell?.textLabel?.text = questionList[indexPath.row]["question"] as? String
        
        cell?.textLabel?.numberOfLines = 0
        cell?.textLabel?.lineBreakMode = .byWordWrapping
        
        return cell!
        
    }
    
    /*
   func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 60.0
    }
    */
    
    
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
