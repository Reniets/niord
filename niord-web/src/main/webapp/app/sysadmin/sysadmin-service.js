
/*
 * Copyright 2017 Danish Maritime Authority.
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

/**
 * The Admin service
 */
angular.module('niord.admin')


    /**
     * ********************************************************************************
     * AdminMessageSeriesService
     * ********************************************************************************
     * Interface for calling message series related functions at the application server
     */
    .factory('AdminMessageSeriesService', [ '$http', function($http) {
        'use strict';

        return {

            /** Returns all message series **/
            getMessageSeries: function () {
                return $http.get('/rest/message-series/all?messageNumbers=true');
            },


            /** Creates a new message series **/
            createMessageSeries: function(series) {
                return $http.post('/rest/message-series/series/', series);
            },


            /** Updates the given message series **/
            updateMessageSeries: function(series) {
                return $http.put('/rest/message-series/series/' + encodeURIComponent(series.seriesId), series);
            },


            /** Deletes the given message series **/
            deleteMessageSeries: function(series) {
                return $http['delete']('/rest/message-series/series/' + encodeURIComponent(series.seriesId));
            },


            /** Returns the next message series number for the given year **/
            getNextMessageSeriesNumber: function (seriesId, year) {
                return $http.get('/rest/message-series/series/' + encodeURIComponent(seriesId) + '/number/' + year);
            },


            /** Sets the next message series number for the given year **/
            updateNextMessageSeriesNumber: function (seriesId, year, num) {
                return $http.put('/rest/message-series/series/' + encodeURIComponent(seriesId) + '/number/' + year, num);
            },


            /** Computes the next message series number for the given year **/
            computeNextMessageSeriesNumber: function (seriesId, year) {
                return $http.get('/rest/message-series/series/' + encodeURIComponent(seriesId) + '/compute-number/' + year);
            }
        };
    }])


    /**
     * ********************************************************************************
     * AdminDomainService
     * ********************************************************************************
     * Interface for calling domain related functions at the application server
     */
    .factory('AdminDomainService', [ '$http', '$rootScope', function($http, $rootScope) {
        'use strict';

        return {

            /** Returns all domain **/
            getDomains: function () {
                return $http.get('/rest/domains/all?inactive=true&lang=' + $rootScope.language);
            },


            /** Creates a new domain **/
            createDomain: function(domain) {
                return $http.post('/rest/domains/domain/', domain);
            },


            /** Updates the given domain **/
            updateDomain: function(domain) {
                return $http.put('/rest/domains/domain/' + encodeURIComponent(domain.domainId), domain);
            },


            /** Deletes the given domain **/
            deleteDomain: function(domain) {
                return $http['delete']('/rest/domains/domain/' + encodeURIComponent(domain.domainId));
            },


            /** Creates the domain in Keycloak **/
            createDomainInKeycloak : function (domain) {
                return $http.post('/rest/domains/keycloak', domain);
            }
        };
    }])


    /**
     * ********************************************************************************
     * AdminScheduleService
     * ********************************************************************************
     * Interface for calling firing schedule related functions at the application server
     */
    .factory('AdminScheduleService', [ '$http', '$rootScope', function($http, $rootScope) {
        'use strict';

        return {

            /** Returns all schedules **/
            getFiringSchedules: function () {
                return $http.get('/rest/firing-schedules/all?lang=' + $rootScope.language);
            },


            /** Creates a new schedule **/
            createFiringSchedule: function(schedule) {
                return $http.post('/rest/firing-schedules/firing-schedule/', schedule);
            },


            /** Updates the given schedule **/
            updateFiringSchedule: function(schedule) {
                return $http.put('/rest/firing-schedules/firing-schedule/' + schedule.id, schedule);
            },


            /** Deletes the given schedule **/
            deleteFiringSchedule: function(schedule) {
                return $http['delete']('/rest/firing-schedules/firing-schedule/' + schedule.id);
            },


            /** Updates the firing exercises based on active schedules **/
            updateFiringExercises: function () {
                return $http.put('/rest/firing-schedules/update-firing-exercises');
            }
        };
    }])


    /**
     * ********************************************************************************
     * AdminDictionariesService
     * ********************************************************************************
     * Interface for calling dictionaries-related functions at the application server
     */
    .factory('AdminDictionariesService', [ '$http', function($http) {
        'use strict';

        return {
            getDictionaryNames: function() {
                return $http.get('/rest/dictionaries/names');
            },

            getDictionaryEntries: function(name) {
                return $http.get('/rest/dictionaries/dictionary/' + encodeURIComponent(name) + '/entries');
            },

            addEntry: function(name, entry) {
                return $http.post('/rest/dictionaries/dictionary/' + encodeURIComponent(name), entry);
            },

            updateEntry: function(name, entry) {
                return $http.put('/rest/dictionaries/dictionary/' + encodeURIComponent(name) + '/'
                    + encodeURIComponent(entry.key), entry);
            },

            deleteEntry: function(name, entry) {
                return $http['delete']('/rest/dictionaries/dictionary/' + encodeURIComponent(name) + '/'
                    + encodeURIComponent(entry.key));
            },

            /** Reloads dictionaries from resource bundles **/
            reloadDictionaries: function() {
                return $http.put('/rest/dictionaries/reload-resource-bundles');
            }
        };
    }])


    /**
     * ********************************************************************************
     * AdminSettingsService
     * ********************************************************************************
     * Interface for calling system settings-related functions at the application server
     */
    .factory('AdminSettingsService', [ '$http', function($http) {
        'use strict';

        return {
            getEditableSettings: function() {
                return $http.get('/rest/settings/editable-settings');
            },

            updateSetting: function(setting) {
                return $http.put('/rest/settings/setting/' + setting.key, setting);
            },

            getSettingsExportTicket: function () {
                return $http.get('/rest/tickets/ticket?role=sysadmin');
            }

        };
    }])


    /**
     * ********************************************************************************
     * AdminBatchService
     * ********************************************************************************
     * Interface for calling batch-related functions at the application server
     */
    .factory('AdminBatchService', [ '$http', function($http) {
        'use strict';

        return {

            /** Returns the batch system status **/
            getBatchStatus: function() {
                return $http.get('/rest/batch/status');
            },

            /** Returns a page full of batch job instances **/
            getBatchInstances: function (name, page, pageSize) {
                var url = '/rest/batch/' + name + '/instances';
                if (pageSize) {
                    url += '?page=' + page + '&pageSize=' + pageSize;
                }
                return $http.get(url);
            },

            /** Stops the given batch execution **/
            stopBatchExecution: function(executionId) {
                return $http.put('/rest/batch/execution/' + executionId + '/stop');
            },

            /** Stops the given batch execution **/
            restartBatchExecution: function(executionId) {
                return $http.put('/rest/batch/execution/' + executionId + '/restart');
            },

            /** Stops the given batch execution **/
            abandonBatchExecution: function(executionId) {
                return $http.put('/rest/batch/execution/' + executionId + '/abandon');
            },

            getBatchDownloadTicket: function () {
                return $http.get('/rest/tickets/ticket?role=admin');
            },

            getBatchLogFiles: function (instanceId) {
                return $http.get('/rest/batch/instance/' + instanceId + '/logs');
            },

            getBatchLogFileContent: function (instanceId, logFileName, fromLineNo) {
                var fromLine = fromLineNo ? '?fromLineNo=' + fromLineNo : '';
                return $http.get('/rest/batch/instance/' + instanceId + '/logs/' + encodeURIComponent(logFileName) + fromLine);
            },

            /** Executes the given JavaScript on the back-end (only Sysadmins) **/
            executeJavaScript: function (scritpName, javascript) {
                return $http.post('/rest/batch/execute-javascript', {
                    scriptName: scritpName,
                    javaScript: javascript
                });
            }

        };
    }]);
