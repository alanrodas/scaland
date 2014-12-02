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

package com.alanrodas.scaland.vcs

trait Revision{def name : String}
case object HEAD extends Revision {def name = "HEAD" }
case object PREV extends Revision {def name = "PREV" }
case object BASE extends Revision {def name = "BASE" }
case object COMMITED extends Revision {def name = "COMMITED" }
