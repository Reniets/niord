
<#assign formatPos = "org.niord.core.fm.directive.LatLonDirective"?new()>
<#assign formatDateInterval = "org.niord.core.fm.directive.DateIntervalDirective"?new()>
<#assign txtToHtml = "org.niord.core.fm.directive.TextToHtmlDirective"?new()>

<!-- ***************************************  -->
<!-- Parametrized message styles             -->
<!-- ***************************************  -->
<#macro pageSizeStyle>
    <style type="text/css" media="all">

        @page {
            size: ${pageSize!"A4"} ${pageOrientation!"portrait"};
        }

    </style>
</#macro>


<!-- ***************************************  -->
<!-- Renders the default headers and footers  -->
<!-- ***************************************  -->
<#macro renderDefaultHeaderAndFooter headerText>
    <div class="first-page-header">
    </div>
    <div class="header">
        <span style="float: left">${headerText}</span>
        <span style="float: right">${text("pdf.page")} <span id="pagenumber"></span></span>
        &nbsp;
    </div>

    <#include "message-list-pdf-footer.ftl">
</#macro>


<!-- ***************************************  -->
<!-- Renders the parent-first area lineage    -->
<!-- ***************************************  -->
<#macro areaLineage area>
    <#if area??>
        <#if area.parent??>
            <@areaLineage area=area.parent /> -
        </#if>
        <#if area.descs?has_content>${area.descs[0].name}</#if>
    </#if>
</#macro>


<!-- ***************************************  -->
<!-- Returns the two root-most area headings  -->
<!-- ***************************************  -->
<#function areaHeading area="area" >
    <#if area?? && (!area.parent?? || (area.parent?? && !area.parent.parent??))>
        <#return area/>
    </#if>
    <#if area?? >
        <#return areaHeading(area.parent) />
    </#if>
    <#return NULL/>
</#function>


<!-- ***************************************  -->
<!-- Returns if the attachment has the given type  -->
<!-- ***************************************  -->
<#function includeAttachment att type >
    <#return att.display?has_content && att.display == type && att.type?has_content && att.type?starts_with('image')/>
</#function>


<!-- ***************************************  -->
<!-- Renders an attachment  -->
<!-- ***************************************  -->
<#macro renderAttachment msg att >
    <#assign imageStyle='max-width: 100%;' />
    <#if att.width?has_content && att.height?has_content>
        <#assign imageStyle="max-width: 100%; width: " + att.width + "; height: " + att.height />
    <#elseif att.width?has_content>
        <#assign imageStyle="max-width: 100%; width: " + att.width + "; " />
    <#elseif att.height?has_content>
        <#assign imageStyle="max-width: 100%; height: " + att.height + "; " />
    </#if>

    <div class="attachment">
        <div>
            <img src="/rest/repo/file/${msg.repoPath}/attachments/${att.fileName}" style="${imageStyle}">
        </div>
        <#if att.descs?has_content && att.descs[0].caption?has_content>
            <div style="margin: 1mm; font-style: italic">${att.descs[0].caption}</div>
        </#if>
    </div>
</#macro>


<!-- ***************************************  -->
<!-- Renders all nessage attachments of the given type    -->
<!-- ***************************************  -->
<#macro renderMessageAttachments msg type>
    <#if msg.attachments?has_content>
        <#list msg.attachments as att>
            <#if includeAttachment(att, type)>
                <@renderAttachment msg=msg att=att />
            </#if>
        </#list>
    </#if>
</#macro>


<!-- ***************************************  -->
<!-- Renders all separate-page attachments    -->
<!-- ***************************************  -->
<#macro renderSeparatePageAttachments msg>
    <#if msg.attachments?has_content>
        <#list msg.attachments as att>
            <#if includeAttachment(att, 'SEPARATE_PAGE')>
            <div class="separate-attachment-page">
                <#assign messageId=msg.id />
                <#if msg.shortId?has_content>
                    <#assign messageId=msg.shortId />
                </#if>
                <div style="margin: 1mm">
                    <h4 style="color: #8f2f7b; font-size: 16px;" id="${messageId}">${text("pdf.attachment.title", messageId)}</h4>
                </div>
                <@renderAttachment msg=msg att=att />
            </div>
            </#if>
        </#list>
    </#if>
</#macro>


<!-- ***************************************  -->
<!-- Renders the TOC for area-headings        -->
<!-- ***************************************  -->
<#macro renderTOC messages areaHeadings prefix="">
    <#if areaHeadings>
        <div>
            <h2>${text("pdf.toc")}</h2>
            <@renderTOCEntries messages=messages prefix=prefix />
        </div>
    </#if>
</#macro>


<#macro renderTOCEntries messages prefix="">
    <#assign tocAreaHeadingId=-9999999 />
    <ol class='toc'>
        <#list messages as msg>
            <#if msg.areas?has_content>
                <#assign area=areaHeading(msg.areas[0]) />
                <#if area?? && area.id != tocAreaHeadingId>
                    <#assign tocAreaHeadingId=area.id />
                    <li><a href='#${prefix}${tocAreaHeadingId?c}'><@areaLineage area=areaHeading(area) /></a></li>
                </#if>
            </#if>
        </#list>
    </ol>
</#macro>


<!-- ***************************************  -->
<!-- Renders a message part                   -->
<!-- ***************************************  -->
<#macro renderMessagePart part>
    <#if part.descs?has_content && part.descs[0].subject??>
        <p><strong>${part.descs[0].subject}</strong></p>
    </#if>
    <#if part.descs?has_content && part.descs[0].details??>
        <div>${part.descs[0].details}</div>
    </#if>
</#macro>


<!-- ***************************************  -->
<!-- Renders a message                        -->
<!-- ***************************************  -->
<#macro renderMessage msg>

    <div style="width: 100%;">

    <!-- Attachments to display above message -->
    <@renderMessageAttachments msg=msg type="ABOVE" />


    <!-- Title line -->
    <#if msg.originalInformation?has_content && msg.originalInformation>
        <div>*</div>
    </#if>

    <#if msg.shortId?has_content>
        <div>
            <span class="label-message-id">${msg.shortId}</span>
        </div>
    </#if>

    <#if msg.descs?has_content && msg.descs[0].title?has_content>
        <div>
            <strong>
                <a href="${baseUri}/#/message/${msg.id}" target="_blank" id="msg_${msg.id}">
                    ${msg.descs[0].title}
                </a>
            </strong>
        </div>
    </#if>


    <table>

        <!-- Reference lines -->
        <#if msg.references?has_content>
            <tr>
                <td class="field-name">
                    ${text("msg.field.references")}
                </td>
                <td class="field-value">
                    <#list msg.references as ref>
                        <div>
                            <a href="${baseUri}/#/message/${ref.messageId?url('ASCII')}" target="_blank">${ref.messageId}</a>

                            <#if ref.type == 'REPETITION'>
                                - ${text("msg.reference.repetition")}
                            <#elseif ref.type == 'CANCELLATION'>
                                - ${text("msg.reference.cancelled")}
                            <#elseif ref.type == 'UPDATE'>
                                - ${text("msg.reference.updated")}
                            </#if>

                            <#if ref.descs?has_content && ref.descs[0].description??>
                                - ${ref.descs[0].description}
                            </#if>
                        </div>
                    </#list>
                </td>
            </tr>
        </#if>


        <!-- Details line -->
        <#if msg.parts?has_content>
            <#list msg.parts as part>
                <tr>
                    <td class="field-name">
                        <#if part?is_first || part.type != msg.parts[part?index - 1].type>
                            ${text("msg.field." + part.type?lower_case)}
                        </#if>
                    </td>
                    <td class="field-value message-description">
                        <@renderMessagePart part=part/>
                    </td>
                </tr>
            </#list>
        </#if>


        <!-- Charts line -->
        <#if msg.charts?has_content>
            <tr>
                <td class="field-name">${text("msg.field.charts")}</td>
                <td class="field-value">
                    <#list msg.charts as chart>
                    ${chart.chartNumber}
                        <#if chart.internationalNumber?has_content>(INT ${chart.internationalNumber?c})</#if>
                        <#if chart_has_next>, </#if>
                    </#list>
                </td>
            </tr>
        </#if>

        <!-- Publication line -->
        <#if msg.descs?has_content && msg.descs[0].publication?has_content>
            <tr>
                <td class="field-name">${text("msg.field.publication")}</td>
                <td class="field-value">
                ${msg.descs[0].publication}
                </td>
            </tr>
        </#if>

        <!-- Source line -->
        <#if msg.descs?has_content && msg.descs[0].source?has_content>
            <tr>
                <td class="field-value" style="text-align: right;" colspan="2">
                    (${msg.descs[0].source})
                </td>
            </tr>
        </#if>

    </table>

    <!-- Attachments to display below message -->
    <@renderMessageAttachments msg=msg type="BELOW" />

    </div>

</#macro>


<!-- ***************************************  -->
<!-- Renders a list of messages               -->
<!-- ***************************************  -->
<#macro renderMessageList messages areaHeadings=true prefix="">

    <#assign areaHeadingId=-9999999 />

    <table class="message-table">
        <!-- Layout row for fixed-layout table -->
        <tr>
            <#if mapThumbnails!true>
                <#assign colspan=2 />
                <td width="140"></td>
                <td width="*"></td>
            <#else>
                <#assign colspan=1 />
                <td width="100%"></td>
            </#if>
        </tr>

        <#list messages as msg>

            <#if msg.areas?has_content>
                <#assign area=areaHeading(msg.areas[0]) />
                <#if areaHeadings && area?? && area.id != areaHeadingId>
                    <#assign areaHeadingId=area.id />
                    <tr>
                        <td colspan="${colspan}" class="table-header"><h4 id="${prefix}${areaHeadingId?c}"><@areaLineage area=areaHeading(area) /></h4></td>
                    </tr>
                </#if>
            </#if>
            <tr>
                <#if mapThumbnails!true>
                    <td class="table-image">
                        <img src="/rest/message-map-image/${msg.id}.png" width="120" height="120"/>
                    </td>
                </#if>
                <td class="table-item">
                    <@renderMessage msg=msg/>
                </td>
            </tr>
        </#list>
    </table>

    <#list messages as msg>
        <@renderSeparatePageAttachments msg=msg />
    </#list>

</#macro>
