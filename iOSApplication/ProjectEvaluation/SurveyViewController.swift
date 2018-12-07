//
//  SurveyViewController.swift
//  AMAD_Assignment2
//
//  Created by Pranalee Jadhav on 9/10/18.
//  Copyright © 2018 Pranalee Jadhav. All rights reserved.
//

import Foundation
import UIKit
import SVProgressHUD

class SurveyViewController: UIViewController{
    
    @IBOutlet weak var question_no: UILabel!
    @IBOutlet weak var questionLb: UILabel!
    
    @IBOutlet weak var option1: UIView!
    @IBOutlet weak var option2: UIView!
    @IBOutlet weak var option3: UIView!
    @IBOutlet weak var option4: UIView!
    @IBOutlet weak var option5: UIView!
    @IBOutlet weak var option1_img: UIImageView!
    @IBOutlet weak var option2_img: UIImageView!
    @IBOutlet weak var option3_img: UIImageView!
    @IBOutlet weak var option4_img: UIImageView!
    @IBOutlet weak var option5_img: UIImageView!
    
    @IBOutlet weak var backBtn: UIButton!
    @IBOutlet weak var submitBtn: UIButton!
    
    var imgArr:[UIImageView]=[UIImageView]()
    
    var qid:Int = 0
    var questionList:[Dictionary<String,Any>] = [Dictionary<String,Any>]()
    var selectedTag:Int = -1
    var answers = [Int](repeating: -1, count: 10)
    var seq:Int = 0
    var ans_arr = ["Poor","Fair","Good","Very Good","Superior"]
    var teamid: Int = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //set navigation bar title
        self.title = "Team " + String(teamid + 1)
        
        imgArr = [option1_img,option2_img,option3_img,option4_img,option5_img]
        
        getData()
        
        var tap = UITapGestureRecognizer(target: self, action: #selector(self.handleTap(_:)))
        option1.addGestureRecognizer(tap)
        tap = UITapGestureRecognizer(target: self, action: #selector(self.handleTap(_:)))
        option2.addGestureRecognizer(tap)
        tap = UITapGestureRecognizer(target: self, action: #selector(self.handleTap(_:)))
        option3.addGestureRecognizer(tap)
        tap = UITapGestureRecognizer(target: self, action: #selector(self.handleTap(_:)))
        option4.addGestureRecognizer(tap)
        tap = UITapGestureRecognizer(target: self, action: #selector(self.handleTap(_:)))
        option5.addGestureRecognizer(tap)

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
                self.questionList = data["questions"] as! [Dictionary<String, Any>]
                print("no of questions \(self.questionList.count)")
                
                self.setQuestion()
            }
        })
    }
    
    func setQuestion(){
        question_no.text = String(seq+1)
        questionLb.text = questionList[qid]["question"] as? String

    }
    
    @objc func handleTap(_ sender: UITapGestureRecognizer) {
        let tag = sender.view?.tag
        imgArr[(tag!-1)].image = UIImage(named: "check.png")
        
        if selectedTag > -1{
            imgArr[selectedTag-1].image = UIImage(named: "uncheck.png")
        }
        selectedTag = tag!
        
    }
    
    @IBAction func submitAns(_ sender: Any) {
        if selectedTag <= -1{
            showMsg(title: "", subTitle: "Please answer the question")
        } else {
            
            answers[qid] = selectedTag-1
            backBtn.isHidden = false
            if submitBtn.currentTitle == "Submit" {
                
                let arraySum = answers.reduce(0) { $0 + $1 }
                //SVProgressHUD.show()
                
                /*post_submitsurvey(parameters: params, handler: {(data) in
                    //dismiss loader
                    
                    SVProgressHUD.dismiss()
                    DispatchQueue.main.async(execute: {
                        let bundle = Bundle.main
                        let storyboard = UIStoryboard(name: "Main", bundle: bundle)
                        
                        //redirect to profile page
                        let newViewController: ShowResultViewController = storyboard.instantiateViewController(withIdentifier: "ShowResultViewController") as! ShowResultViewController
                        newViewController.scores = arraySum
                        self.navigationController?.pushViewController(newViewController, animated: true)
                        
                    })
                })*/
                //show loader
               /* SVProgressHUD.show()
                getAPIRequest(server_api: "score", handler: {(data) in
                    //dismiss loader
                    SVProgressHUD.dismiss()
                    if let val = data["code"] as? Int{
                        switch(val){
                        case 0: self.showMsg(title: "Oops!", subTitle: "No Internet")
                            break
                            
                        default:
                            self.showMsg(title: "Error", subTitle: "Please try again")
                        }
                    } else {
                        
                        /*self.tableArray = data["teams"] as! [Dictionary<String, Any>]
                        self.tableView.reloadData()*/
                    }
                })*/
                self.closeSurvey(title: "Thank you for Evaluating Team \(teamid + 1)", subTitle: "You graded \(arraySum)")
                
            } else {
                seq = seq + 1
                qid = qid + 1
                print("qid \(qid)")
                print("new question")
                if qid == (questionList.count-1){
                    submitBtn .setTitle("Submit", for: UIControl.State.normal)
                }
                if selectedTag > -1{
                    imgArr[selectedTag-1].image = UIImage(named: "uncheck.png")
                }
                setQuestion()
                if answers[qid] > -1 {
                    imgArr[answers[qid]].image = UIImage(named: "check.png")
                }
            }
            selectedTag = -1
        }
    }
    
    
    @IBAction func goBack(_ sender: Any) {
        seq = seq - 1
        answers[qid] = selectedTag-1
        submitBtn .setTitle("Next", for: UIControl.State.normal)
        if qid==1 {
            backBtn.isHidden = true
            
        } else {
            backBtn.isHidden = false
            backBtn .setTitle("Back", for: UIControl.State.normal)
        }
        qid = qid - 1
        print("new back qid")
        print(qid)
        setQuestion()
        if selectedTag > -1{
            imgArr[selectedTag-1].image = UIImage(named: "uncheck.png")
        }
        if answers[qid] > -1 {
            imgArr[answers[qid]].image = UIImage(named: "check.png")
        }
        
        selectedTag = answers[qid] + 1
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
    
    //show alertbox
    func closeSurvey(title: String, subTitle: String) -> Void {
        DispatchQueue.main.async(execute: {
            let alertController = UIAlertController(title: title, message:
                subTitle, preferredStyle: UIAlertController.Style.alert)
            alertController.addAction(UIAlertAction(title: "Okay", style: UIAlertAction.Style.default,handler: { action in self.performSegue(withIdentifier: "goBackHome", sender: self) }))
            self.present(alertController, animated: true, completion: nil)
        })
        
    }
    
}