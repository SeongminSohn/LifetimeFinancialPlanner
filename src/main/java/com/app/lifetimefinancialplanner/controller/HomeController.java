package com.app.lifetimefinancialplanner.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@Tag(name = "Home API", description = "Endpoints for the home page and general navigation")
public class HomeController {

}
