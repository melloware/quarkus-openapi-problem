# Introduction

This package contains the test classes used to validate the correct generation of the OpenAPI HttpProblem.

This module will make use of the `generated-clients` dependency and use the api classes to call the various services at test time and validate the results.

#### NOTE: I was hoping these tests could be used to start up the service backends but right now it's not doing that and I'm not sure why. So they will have to be manually started until I get that working.

### Services
* Gateway - Main entry point to call backend services SVC1 and SVC2
* Service One - Calls into SVC2 to simulate downstream calls
* Service Two - Last service in the chain
