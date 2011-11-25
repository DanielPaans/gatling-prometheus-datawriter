/*
 * Copyright 2011 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.core.feeder

import scala.collection.immutable.HashMap
import scala.collection.mutable.Queue
import scala.io.Source
import scala.tools.nsc.io.Path.string2path

import com.excilys.ebi.gatling.core.config.GatlingConfig.CONFIG_ENCODING
import com.excilys.ebi.gatling.core.config.GatlingFiles.GATLING_DATA_FOLDER

/**
 * CSV Generic implementation of Feeders
 *
 * Upon contruction, children classes will load all values into memory
 *
 * @param fileName the file in which the values are
 * @param mappings list of keys that will match different columns
 * @param separator the separator used by this CSV file (tabulation, comma, etc.)
 * @param extension the extension of the file
 */
abstract class SeparatedValuesFeeder(fileName: String, separator: String, extension: String) extends Feeder(fileName) {

	var seeds: Queue[Map[String, String]] = Queue()

	val feederFileLines = Source.fromFile(GATLING_DATA_FOLDER / fileName + extension, CONFIG_ENCODING).getLines

	val getHeaders: List[String] = feederFileLines.next.split(separator).toList

	for (line <- feederFileLines) {
		var lineMap = new HashMap[String, String]

		for (mapping <- getHeaders zip line.split(separator).toList)
			lineMap = lineMap + mapping

		seeds += lineMap
	}

	logger.debug("Feeder Seeds Loaded")

	def next: Map[String, String] = seeds.dequeue
}