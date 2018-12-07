//
//  SignInViewController.swift
//  ProjectEvaluation
//
//  Created by Pranalee Jadhav on 11/30/18.
//  Copyright © 2018 Pranalee Jadhav. All rights reserved.
//

import UIKit


class SignInViewController: UIViewController {

    @IBOutlet var mainView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    @IBAction func unwindToLoginPage(segue: UIStoryboardSegue) {
        dismiss(animated: true, completion: nil)
    }

}
