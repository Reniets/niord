/*
 * Copyright 2016 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.niord.web.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.niord.core.NiordApp;
import org.niord.model.message.MainType;
import org.niord.model.message.MessageVo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A public REST API for accessing Niord data.
 * <p>
 * Also, defined the Swagger API using annotations.
 */
@Api(value = "/public/v1",
     description = "Public API for accessing the Niord NW-NM system",
     tags = {"message_list"})
@Path("/public/v1")
@Stateless
@SuppressWarnings("unused")
public class ApiRestService extends AbstractApiService {

    @Inject
    NiordApp app;

    /** {@inheritDoc} */
    @ApiOperation(
            value = "Returns the public NW and NM messages",
            response = MessageVo.class,
            responseContainer = "List"
    )
    @GET
    @Path("/messages")
    @Produces({"application/json;charset=UTF-8", "application/xml;charset=UTF-8"})
    @GZIP
    @NoCache
    @Override
    public List<MessageVo> search(
            @ApiParam(value = "Two-letter ISO 639-1 language code", example="en")
            @QueryParam("lang") String language,

            @ApiParam(value = "The ID of the domain to select messages from", example="niord-client-nw")
            @QueryParam("domain") String domain,

            @ApiParam(value = "Specific message series to select messages from", example="dma-nw")
            @QueryParam("messageSeries") Set<String> messageSeries,

            @ApiParam(value = "Either NW (navigational warnings) or NM (notices to mariners)", example="NW")
            @QueryParam("mainType") Set<MainType> mainTypes,

            @ApiParam(value = "Well-Known Text for geographical extent", example="POLYGON((7 54, 7 57, 13 56, 13 57, 7 54))")
            @QueryParam("wkt") String wkt

    ) throws Exception {
        return super.search(language, domain, messageSeries, mainTypes, wkt);
    }


    /**
     * Returns the XSD for the Message class.
     * Two XSDs are produced, "schema1.xsd" and "schema2.xsd". The latter is the main schema for
     * Message and will import the former.
     * @return the XSD for the Message class
     */
    @ApiOperation(
            value = "Returns XSD model of the Message class"
    )
    @GET
    @Path("/xsd/{schemaFile}")
    @Produces("application/xml;charset=UTF-8")
    @GZIP
    @NoCache
    public String getMessageXsd(
            @ApiParam(value = "The schema file, either schema1.xsd or schema2.xsd", example="schema2.xsd")
            @PathParam("schemaFile")
            String schemaFile) throws Exception {

        if (!schemaFile.equals("schema1.xsd") && !schemaFile.equals("schema2.xsd")) {
            throw new Exception("Only 'schema1.xsd' and 'schema2.xsd' allowed");
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(MessageVo.class);

        Map<String, StringWriter> result = new HashMap<>();
        SchemaOutputResolver sor = new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
                StringWriter out = new StringWriter();
                result.put(suggestedFileName, out);
                StreamResult result = new StreamResult(out);
                result.setSystemId(app.getBaseUri() + "/rest/public/v1/xsd/" + suggestedFileName);
                return result;
            }

        };

        // Generate the schemas
        jaxbContext.generateSchema(sor);

        return result.get(schemaFile).toString();
    }
}
