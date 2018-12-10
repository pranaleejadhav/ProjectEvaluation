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
        self.title = "Project Details"
        
        if let baseURL = Bundle.main.resourceURL {
            let fileURL = baseURL.appendingPathComponent("Project.html")
            
            webView.loadFileURL(fileURL, allowingReadAccessTo: fileURL)
        }
        
    }
    

    

}
