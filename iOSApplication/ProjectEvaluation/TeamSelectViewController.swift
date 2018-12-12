//
//  TeamSelectViewController.swift
//  ProjectEvaluation
//
//  Created by Pranalee Jadhav on 11/30/18.
//  Copyright © 2018 Pranalee Jadhav. All rights reserved.
//

import UIKit
import SVProgressHUD

class itemTableViewCell: UITableViewCell {
    
    @IBOutlet weak var itemImage: UIImageView!
    @IBOutlet weak var itemName: UILabel!
    @IBOutlet weak var discount: UILabel!
    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var region: UILabel!
    
}

class TeamSelectViewController: UIViewController, UITableViewDataSource,UITableViewDelegate {

    @IBOutlet weak var viewLeading: NSLayoutConstraint!
    @IBOutlet weak var viewTrailing: NSLayoutConstraint!
    var hamburgerMenuIsVisible = false
    
    @IBOutlet weak var eName: UILabel!
    @IBOutlet weak var stackLeading: NSLayoutConstraint!
    
    @IBOutlet weak var tableView: UITableView!
    var tableArray1 = [Dictionary<String, Any>]()
    var tableArray2 = [Dictionary<String, Any>]()
    var originalArr = [Dictionary<String, Any>]()
    @IBOutlet weak var segmentedCtrl: UISegmentedControl!
    
    @IBOutlet weak var sideView: UIView!
    @IBOutlet weak var mainView: UIView!
    var userid: String = ""
    var drawerOffset:CGFloat = -250
    var deviceType = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Select Team"
        navigationController?.navigationBar.barTintColor = #colorLiteral(red: 0.9215686275, green: 0.3568627451, blue: 0.3333333333, alpha: 1)
        navigationController?.navigationBar.tintColor = UIColor.white
        let textAttributes = [NSAttributedString.Key.foregroundColor:UIColor.white]
        navigationController?.navigationBar.titleTextAttributes = textAttributes

        var backBtn: UIButton!
        backBtn = UIButton(frame: CGRect(x: 0, y: 0, width: 80, height: 25))
        backBtn.setImage(UIImage(named: "drawer"), for: UIControl.State.normal)
        backBtn.addTarget(self, action: #selector(leftBtnPressed), for: .touchUpInside)
        backBtn.contentHorizontalAlignment = .left
        backBtn.imageEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 10);
        backBtn.titleEdgeInsets = UIEdgeInsets(top: 0, left: 10, bottom: 0, right: 0);
        backBtn.tintColor = .white
        backBtn.setTitleColor(.white, for: UIControl.State.normal)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(customView: backBtn)
        
        tableView.separatorColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
        tableView.dataSource = self
        tableView.delegate = self
        tableView.tableFooterView = UIView()
        
        //Now for changing the title:-
        segmentedCtrl.setTitle("Non-Evaluated", forSegmentAt: 0)
        segmentedCtrl.setTitle("Evaluated", forSegmentAt: 1)
        // Do any additional setup after loading the view.
        
        sideView.layer.masksToBounds = false
        sideView.layer.shadowRadius = 4
        sideView.layer.shadowOpacity = 1
        sideView.layer.shadowColor = UIColor.gray.cgColor
        sideView.layer.shadowOffset = CGSize(width: 3 , height:0)
        
        userid = UserDefaults.standard.string(forKey: "userid") ?? ""
        eName.text = "Hello, \(userid)"
        
        
        if UIDevice.current.userInterfaceIdiom == .pad {
            drawerOffset = -350
            deviceType = false
        }
       
        
        getData()
    }
    
    @IBAction func closeSideView(_ sender: Any) {
        
        stackLeading.constant = drawerOffset;
        
        //1
        hamburgerMenuIsVisible = false
        self.mainView.isUserInteractionEnabled = true
    }
    
    func getData() {
        
        originalArr.removeAll()
        self.tableArray1.removeAll()
        self.tableArray2.removeAll()
        //show loader
        SVProgressHUD.show()
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
                if ((data["message"] as? String) != nil) && ((data["message"] as? String) == "Invalid user") {
                    self.showMsg(title: "Error", subTitle: "Please try again")
                } else {
                self.tableArray2 = data["teams"] as! [Dictionary<String, Any>]
                
                getAPIRequest(server_api: "teams", handler: {(data) in
                    
                    print("DATA \(data)")
                    
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
                        
                        arr.forEach{ (item:Dictionary<String, Any>) in
                            let str = item["teamId"] as? String
                            if !self.tableArray2.contains(where: {$0["teamId"] as? String  == str }) {
                                self.tableArray1.append(item)
                            }
                        
                        }
                        self.originalArr = self.tableArray1
                        self.tableView.reloadData()
                    }
                })
                    
                }
                
                
               
            }
        })
    }

    @IBAction func leftBtnPressed(_ sender: Any) {
        //if the hamburger menu is NOT visible, then move the ubeView back to where it used to be
        if hamburgerMenuIsVisible {
            //viewLeading.constant = 150
            //this constant is NEGATIVE because we are moving it 150 points OUTWARD and that means -150
            //viewTrailing.constant = -150
            //stackLeading.constant = -250;
             stackLeading.constant = drawerOffset;
            //1
            hamburgerMenuIsVisible = false
            self.mainView.isUserInteractionEnabled = true
            
        } else {
            //if the hamburger menu IS visible, then move the ubeView back to its original position
            //viewLeading.constant = 0
            //viewTrailing.constant = 0
            stackLeading.constant = 0;
            
            //2
            hamburgerMenuIsVisible = true
            self.mainView.isUserInteractionEnabled = false
            
        }
        UIView.animate(withDuration: 0.2, delay: 0.0, options: .curveEaseIn, animations: {
            self.view.layoutIfNeeded()
        }) { (animationComplete) in
            print("The animation is complete!")
        }
        
        
    }
    
    
    @IBAction func toggleList(_ sender: Any) {
        if segmentedCtrl.selectedSegmentIndex == 0 {
            originalArr = tableArray1
        } else {
            originalArr = tableArray2
        }
        tableView.reloadData()
    }
    
    
    override func viewDidDisappear(_ animated: Bool) {
         stackLeading.constant = drawerOffset;
        
        hamburgerMenuIsVisible = false
        self.mainView.isUserInteractionEnabled = true
        super.viewDidDisappear(true)
    }

    @IBAction func logout_evaluator(_ sender: Any) {
        UserDefaults.standard.removeObject(forKey: "token")
        UserDefaults.standard.removeObject(forKey: "userid")
        print("logout done")
        NotificationCenter.default.post(name: Notification.Name("com.amad.final_proj"), object: self, userInfo: nil)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return originalArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellItem") //1.
        cell?.textLabel?.text = originalArr[indexPath.row]["teamId"] as? String
        
        return cell!
        
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if deviceType {
            return 60.0
        } else {
            return 80.0
        }
        
        
    }

    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        performSegue(withIdentifier: "showquestions", sender: indexPath)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showquestions" {
            
            let loc = sender as! IndexPath
            let vc = segue.destination as! SurveyViewController
            //print(tableArr[loc.row].id)
            vc.teamid = (originalArr[loc.row]["teamId"] as? String)!

        } else  if segue.identifier == "showQRPage" {
            let vc = segue.destination as! QRScannerViewController
            vc.escanType = false
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
    
    
    
    @IBAction func unwindToHome(segue:UIStoryboardSegue) {
        segmentedCtrl.setEnabled(true, forSegmentAt: 0)
        segmentedCtrl.selectedSegmentIndex = 0
        print("jgjhggj")
        getData()
    }
    
}


