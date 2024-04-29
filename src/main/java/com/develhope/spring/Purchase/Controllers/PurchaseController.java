package com.develhope.spring.Purchase.Controllers;

import com.develhope.spring.Purchase.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;
}
