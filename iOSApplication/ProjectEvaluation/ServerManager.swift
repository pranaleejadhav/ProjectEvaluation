//
//  ServerManager.swift
//  BestBookSeller
//
//  Created by Pranalee Jadhav on 10/23/18.
//  Copyright © 2018 Pranalee Jadhav. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON


//check internet connectivity
class Connectivity {
    class var isConnectedToInternet:Bool {
        return NetworkReachabilityManager()!.isReachable
    }
}

let server_url = "http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/"

// server call to get data
/*func getData(server_api: String, parameters: String, onSuccess: @escaping (Any?)-> Void, onFail : @escaping (Error?) ->(Void)){
    
    let url = server_url + server_api + "?api-key=1a1f6166121741e5b936cd00d48ace59" + parameters
    
    Alamofire.request(url).responseJSON { (response:DataResponse<Any>) in
        
        switch response.result {
        case .success(let value):
            //print(value)
            onSuccess(value)
            
            break
            
        case .failure(let error):
            onFail(error)
            break
        }
    }
    
    
}*/

//to call get data api
func getAPIRequest(server_api: String, handler:@escaping (Dictionary<String, Any>) -> Void) -> Void {
    let headers = [
        "Authorization": "Bearer "+UserDefaults.standard.string(forKey: "token")!,
        "Content-Type": "application/json"
    ]
    if Connectivity.isConnectedToInternet {
        let url = server_url + server_api
        
        Alamofire.request(url, method: .get, parameters: nil,encoding:
            JSONEncoding.default, headers: headers).responseJSON { (response:DataResponse<Any>) in
                print(response)
                switch response.result {
                case .success(let value):
                    if let val = value as? Dictionary<String, Any> {
                        
                        handler(val)
                        
                    }else {
                        handler(["code":1])
                    }
                    break
                    
                case .failure(let error):
                    print(error)
                    handler(["code":1])
                }
        }
    }else {
        handler(["code":"0"])
    }
}


func getTeamAPIRequest(server_api: String, handler:@escaping (Dictionary<String, Any>) -> Void) -> Void {
    let headers = [
        "Authorization": "Bearer "+UserDefaults.standard.string(forKey: "team_token")!,
        "Content-Type": "application/json"
    ]
    if Connectivity.isConnectedToInternet {
        let url = server_url + server_api
        
        Alamofire.request(url, method: .get, parameters: nil,encoding:
            JSONEncoding.default, headers: headers).responseJSON { (response:DataResponse<Any>) in
                print(response)
                switch response.result {
                case .success(let value):
                    if let val = value as? Dictionary<String, Any> {
                        
                        handler(val)
                        
                    }else {
                        handler(["code":1])
                    }
                    break
                    
                case .failure(let error):
                    print(error)
                    handler(["code":1])
                }
        }
    }else {
        handler(["code":"0"])
    }
}

//to call login api
func post_loginrequest(parameters: Parameters, handler:@escaping (Int) -> Void) -> Void {
    if Connectivity.isConnectedToInternet {
        
        let url = server_url + "usertoken"
        Alamofire.request(url, method: .get).validate().responseJSON { response in
            /*switch response.result {
            case .success(let value):
                let json = JSON(value)
                var js = json["jwt"]
                print("JSONghjgjhgj: \(js)")
            case .failure(let error):
                print(error)
            }
        }*/

        /*Alamofire.request(url)
            .responseString { response in
                guard let responseString = response.result.value else {
                    print("ERROR: didn't get a string in the response")
                    return
                }
                */
       
                
                switch response.result {
                case .success(let value):
                    print("response")
                    let json = JSON(value)
                    let val = json["jwt"].rawString() //.stringValue
                    UserDefaults.standard.set(val, forKey: "token")
                    
                    
                   // print("kjhk kjhkbk \(val)")
                     handler(2)
                    
                    break
                    
                case .failure(let error):
                    print(error)
                    handler(1)
                    
                }
        }
        
        
    }
    else {
        handler(0)
    }
}
