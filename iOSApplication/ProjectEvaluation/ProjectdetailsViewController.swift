//
//  ProjectdetailsViewController.swift
//  ProjectEvaluation
//
//  Created by Pranalee Jadhav on 12/8/18.
//  Copyright © 2018 Pranalee Jadhav. All rights reserved.
//

import UIKit
import WebKit

class ProjectdetailsViewController: UIViewController {

    @IBOutlet weak var webView: WKWebView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if let url = URL(string: "https://www.apple.com") {
            let request = URLRequest(url: url)
            webView.load(request)
        }
        
    }
    

    

}
