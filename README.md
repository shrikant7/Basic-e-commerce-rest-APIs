# Basic-e-commerce-rest-APIs
[![app](https://img.shields.io/badge/app-Swagger-green.svg?style=flat-square)](https://extreme-height-278913.el.r.appspot.com/swagger-ui.html)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg?style=flat-square)](https://raw.githubusercontent.com/shrikant7/Basic-e-commerce-rest-APIs/master/LICENSE)
[![author](https://img.shields.io/badge/author-Shrikant%20Sharma-lightgrey.svg?colorB=9900cc&style=flat-square)](https://www.linkedin.com/in/shrikant007/)

Application is providing full backend support for general ecommerce web/mobile app where client required to have REST APIs ranging from user management, catalog management, cart management to order management.
It includes Authentication, authorization, mail notification, swagger integration and JWT based stateless request handling. 

This project has been deployed on google cloud **App Engine** form local during development process and also using gcloud mysql instance and gcloud bucket for image storage. It follows _"Richardson Maturity level 2"_ rest compliance.

#### To develop on this project you need to have installed following things:
* Intellij Idea - Any IDE for development
* Git - to pick related commit
* Mysql - for local development or as a client for gcloud sql instance
* JDK - development and debugging java app
* Google cloud SDK - for CLI based commands like deploying to app engine or connecting to gcloud sql

Please do above with a project on google cloud platform, authenticate yourself on gcloud using GCLOUD SDK, whitelist your public ip to connect to gcloud sql instance and configure _**application.properties**_ file with your credentials.
