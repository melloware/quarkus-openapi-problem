# Introduction

This package contains the three test services use to validate the correct generation of the OpenAPI HttpProblem.

Each service plays a part in the overall system test of verifying proper use of HttpProblem and propagation of nested HttpProblem instances.

Each of these modules will generate their own `openapi-spec` in the `generated-openapi-specs` folder so the `generated-clients` module has access to all of the files. I couldn't find a way to tell the generator to look in each module so I had to move each one into a global folder.

### Services
* Gateway - Main entry point to call backend services SVC1 and SVC2
* Service One - Calls into SVC2 to simulate downstream calls
* Service Two - Last service in the chain
