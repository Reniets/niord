/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.niord.core.message.batch;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.niord.core.batch.AbstractItemHandler;
import org.niord.core.util.JsonUtils;
import org.niord.model.vo.MessageVo;

import javax.inject.Named;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Imports a list of messages from the messages.json file.
 * <p>
 * Please note, the actual message-import.xml job file is not placed in the META-INF/batch-jobs of this project,
 * but rather, in the META-INF/batch-jobs folder of the niord-web project.<br>
 * This is because of a class-loading bug in the Wildfly implementation. See e.g.
 * https://issues.jboss.org/browse/WFLY-4988
 * <p>
 * Format of json file is defined by the MessageVo class. Example:
 * <pre>
 * [
 *  {
 *    "version" : 0,
 *    "number" : 1279,
 *    "type" : "PERMANENT_NOTICE",
 *    "areas" : [ {
 *      "parent" : {
 *        "descs" : [ {
 *          "lang" : "da",
 *          "name" : "Danmark"
 *         }, {
 *          "lang" : "en",
 *          "name" : "Denmark"
 *         } ]
 *      },
 *      "descs" : [ {
 *        "lang" : "da",
 *        "name" : "Sundet"
 *      }, {
 *        "lang" : "en",
 *        "name" : "The Sound"
 *      } ]
 *      } ],
 *    "charts" : [ {
 *      "chartNumber" : "134",
 *      "internationalNumber" : 1334,
 *      "fullChartNumber" : "134 (INT 1334)"
 *    } ],
 *    "geometry" : {
 *      "type" : "FeatureCollection",
 *      "features" : [ {
 *        "type" : "Feature",
 *          "geometry" : {
 *            "type" : "MultiPoint",
 *            "coordinates" : [ [ 12.639433333333333, 55.6765 ], [ 12.640516666666667, 55.67843333333333 ] ]
 *          },
 *          "properties" : { }
 *        } ]
 *      },
 *    "references" : [ {
 *      "messageId" : "NM-845-15",
 *      "type" : "CANCELLATION"
 *    } ],
 *    "atonUids" : [ ],
 *    "originalInformation" : true,
 *    "descs" : [ {
 *      "lang" : "da",
 *      "title" : "Danmark. Sundet. Københavns Havn. Prøvesten. Kaj 860 - 861. Vedligeholdelsesarbejde afsluttet.",
 *      "subject" : "Prøvesten. Kaj 860 - 861. Vedligeholdelsesarbejde afsluttet.",
 *      "vicinity" : "Københavns Havn",
 *      "publication" : "www.danskehavnelods.dk. [J.nr. 2015018606].",
 *      "source" : "Per Aarsleff A/S 1. december 2015"
 *    }, {
 *      "lang" : "en",
 *      "title" : "Denmark. The Sound. Copenhagen Harbour. Prøvesten. Quay 860 - 861. Maintenance work completed.",
 *      "subject" : "Prøvesten. Quay 860 - 861. Maintenance work completed.",
 *      "vicinity" : "Copenhagen Harbour",
 *      "publication" : "www.danskehavnelods.dk.",
 *      "source" : "Per Aarsleff A/S 1 December 2015"
 *    }
 *  },
 *  ...
 * ]
 * </pre>
 */
@Named
public class BatchMessageImportReader extends AbstractItemHandler {

    List<MessageVo> messages;
    int messageNo = 0;


    /** {@inheritDoc} **/
    @Override
    public void open(Serializable prevCheckpointInfo) throws Exception {

        // Validate that we have access to the "seriesId" and "batchName" properties
        Properties props = job.readProperties();
        if (StringUtils.isBlank(props.getProperty("seriesId"))) {
            getLog().severe("Missing seriesId batch property");
            throw new Exception("Missing seriesId batch property");
        }
        if (StringUtils.isBlank(props.getProperty("tagName"))) {
            getLog().severe("Missing tagName batch property");
            throw new Exception("Missing tagName batch property");
        }

        messages = readMessages();

        if (prevCheckpointInfo != null) {
            messageNo = (Integer) prevCheckpointInfo;
        }

        getLog().info("Start processing " + messages.size() + " messages from index " + messageNo);
    }


    /** Reads in the batch import messages */
    protected List<MessageVo> readMessages() throws Exception {

        // Default implementation reads the messages from a message.json batch file
        Path path = batchService.getBatchJobDataFile(jobContext.getInstanceId());

        // Load the charts from the file
        return JsonUtils.readJson(
                new TypeReference<List<MessageVo>>(){},
                path);
    }


    /** {@inheritDoc} **/
    @Override
    public Object readItem() throws Exception {
        if (messageNo < messages.size()) {

            // Every now and then, update the progress
            if (messageNo % 10 == 0) {
                updateProgress((int)(100.0 * messageNo / messages.size()));
            }

            getLog().info("Reading legacy NM no " + messageNo);
            return messages.get(messageNo++);
        }
        return null;
    }


    /** {@inheritDoc} **/
    @Override
    public Serializable checkpointInfo() throws Exception {
        return messageNo;
    }

}
