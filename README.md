# Katerm JVM
[![Build][github-build-badge]][github-build]
[![License][license-badge]][license]
[![Maven Release][maven-release-badge]][maven-release]
[![GitHub Release][github-release-badge]][github-release]

An advanced term library.

## Usage
Add this library as a dependency to your project.

### Gradle
```kotlin
dependencies {
    implementation("net.pelsmaeker:katerm:0.0.5")
}
```

### Maven
```xml
<dependency>
    <groupId>net.pelsmaeker</groupId>
    <artifactId>katerm</artifactId>
    <version>0.0.5</version>
</dependency>
```

## Design
This term library supports the following kinds of terms:

- Constructor application terms (`ApplTerm`)
- String terms (`StringTerm`)
- Integer value terms (`IntTerm`)
- Real value terms (`RealTerm`)
- List terms (`ListTerm`)
- Term variables (`TermVar`)

### List terms
List terms can be implemented as cons-nil lists, or as arrays.

## License
Copyright © 2023-2024 Daniel A. A. Pelsmaeker

Licensed under the Apache License, Version 2.0 (the "License"); you may not use files in this project except in compliance with the License. You may obtain a copy of the License at <https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an **"as is" basis, without warranties or conditions of any kind**, either express or implied. See the License for the specific language governing permissions and limitations under the License.

[github-build-badge]: https://github.com/Virtlink/katerm/actions/workflows/build.yaml/badge.svg
[github-build]: https://github.com/Virtlink/katerm/actions
[license-badge]: https://img.shields.io/github/license/Virtlink/katerm
[license]: https://github.com/Virtlink/katerm/blob/main/LICENSE
[maven-release-badge]: https://img.shields.io/maven-central/v/net.pelsmaeker/katerm
[maven-release]: https://mvnrepository.com/artifact/net.pelsmaeker/katerm
[github-release-badge]: https://img.shields.io/github/v/release/Virtlink/katerm
[github-release]: https://github.com/Virtlink/katerm/releases
