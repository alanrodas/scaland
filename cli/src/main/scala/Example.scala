/*
 * Copyright 2014 Alan Rodas Bonjour
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

import com.alanrodas.scaland.cli._
import language.postfixOps

object MainApp extends CLIApp {
  root accepts (
      flag named "verbose" alias "v",
      flag named "quiet" alias "q",
      flag named "dry-run",
      flag named "force" alias "f",
      arg named "repo" taking 1 value,
      arg named "recurse-submodules" taking 1 as "check",
      arg named "exec" taking 2 values
      ) receives (
      value named "target" mandatory,
      value named "branch" as "master"
      ) does { commandCall =>
    dump(commandCall)
  }
  command named "add" accepts (
      flag named "quiet" alias "q",
      flag named "dry-run" alias "n",
      flag named "force" alias "f",
      flag named "intent-to-add",
      flag named "all" alias "A"
      ) minimumOf 1 does { commandCall =>
    dump(commandCall)
  }
}
