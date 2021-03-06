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

package org.niord.core.promulgation.vo;

import org.apache.commons.lang.StringUtils;
import org.niord.core.promulgation.TwitterMessagePromulgation;

/**
 * Defines the value object data associated with Twitter promulgation
 */
@SuppressWarnings("unused")
public class TwitterMessagePromulgationVo extends BaseMessagePromulgationVo<TwitterMessagePromulgation> {

    String tweet;

    /** Constructor **/
    public TwitterMessagePromulgationVo() {
        super();
    }


    /** Constructor **/
    public TwitterMessagePromulgationVo(PromulgationTypeVo type) {
        super(type);
    }


    /** {@inheritDoc} **/
    @Override
    public TwitterMessagePromulgation toEntity() {
        return new TwitterMessagePromulgation(this);
    }


    /** {@inheritDoc} **/
    @Override
    public boolean promulgationDataDefined() {
        return StringUtils.isNotBlank(tweet);
    }


    /** Resets data of this message promulgation **/
    public TwitterMessagePromulgationVo reset() {
        tweet = null;
        return this;
    }

    /*************************/
    /** Getters and Setters **/
    /*************************/

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }
}
