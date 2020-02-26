package org.gserve.api.logging;
/*
 *  Copyright (C) 2020 Dustin K. Redmond
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides means to send SMS messages via a subscription to the Twilio service.
 * @author Dustin K. Redmond
 * @since 02/26/2020 08:34
 */
public class SimpleSMS {

    private String fromNumber;

    /**
     * Creates an instance and authenticates with the Twilio SMS service.
     * @param accountSID Twilio provided Account SID
     * @param authToken Twilio provided Auth Token
     * @param fromNumber Phone number to send the message from in format "+{country code}{area code}{number}" e.g. "+15551234567"
     */
    public SimpleSMS(String accountSID, String authToken, String fromNumber) {
        this.fromNumber = fromNumber;
        Twilio.init(accountSID, authToken);
    }

    /**
     * Sends a single message via Twilio API
     * @param toNumber Phone number of the recipient in format "+{country code}{area code}{number}" e.g. "+15551234567
     * @param message SMS Message to send
     */
    public TwilioResponse sendSms(String toNumber, String message) {
        Message sms = Message.creator(new PhoneNumber(toNumber), new PhoneNumber(this.fromNumber), message).create();
        return mapToResponse(sms);
    }

    public List<TwilioResponse> sendSms(String[] toNumbers, String message) {
        List<TwilioResponse> responses = new ArrayList<>();
        for (String number: toNumbers) {
            responses.add(mapToResponse(
                    Message.creator(new PhoneNumber(number),new PhoneNumber(this.fromNumber), message).create()));
        }
        return responses;
    }

    private TwilioResponse mapToResponse(Message msg) {
        HashMap<String,String> map = new HashMap<>();
        // NOTE: Found that the Message getters can return null -dustinkredmond 2020-02-26
        map.put("uri", notNull(msg.getUri()));
        map.put("to", notNull(msg.getTo()));
        map.put("from", notNull(msg.getFrom()));
        map.put("status", notNull(msg.getStatus()));
        map.put("errorCode", notNull(msg.getErrorCode()));
        return new TwilioResponse(map);
    }

    private static class TwilioResponse {
        private HashMap<String,String> response;
        public TwilioResponse(HashMap<String,String> response) {
            this.response = response;
        }

        /**
         * Returns a map of the Response codes from the Twilio Message
         * Format: responseType:responseValue
         * @return Map of the message response codes.
         */
        public HashMap<String,String> getResponse() { return this.response; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Message:{");
            this.response.forEach((k,v) -> sb.append("(").append(k).append(",").append(v).append(")"));
            sb.append("}");
            return sb.toString();
        }
    }

    private String notNull(Object o) {
        return o == null ? "null" : o.toString();
    }
}
