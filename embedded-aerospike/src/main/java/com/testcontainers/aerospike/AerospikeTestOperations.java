/*
* The MIT License (MIT)
*
* Copyright (c) 2019 Playtika
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */
package com.testcontainers.aerospike;

import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class AerospikeTestOperations {

    private final ExpiredDocumentsCleaner expiredDocumentsCleaner;
    private final GenericContainer aerospike;

    public void addNetworkLatencyForResponses(long millis) {
        executeCommandInContainer(() -> {
                    try {
                        return aerospike.execInContainer("tc", "qdisc", "add", "dev", "eth0", "root", "netem", "delay", millis + "ms");
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to execute command", e);
                    }
                }
        );
    }

    public void removeNetworkLatencyForResponses() {
        executeCommandInContainer(() -> {
            try {
                return aerospike.execInContainer("tc", "qdisc", "del", "dev", "eth0", "root");
            } catch (Exception e) {
                throw new IllegalStateException("Failed to execute command", e);
            }
        });
    }

    public void addDuration(Duration duration) {
        timeTravel(DateTime.now().plus(duration).plusMinutes(1));
    }

    public void timeTravelTo(DateTime futureTime) {
        DateTime now = DateTime.now();
        if (futureTime.isBeforeNow()) {
            throw new IllegalArgumentException("Time should be in future. Now is: " + now + " time is:" + futureTime);
        } else {
            timeTravel(futureTime);
        }
    }

    public void rollbackTime() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    private void timeTravel(DateTime newNow) {
        DateTimeUtils.setCurrentMillisFixed(newNow.getMillis());
        expiredDocumentsCleaner.cleanExpiredDocumentsBefore(newNow.getMillis());
    }

    private void executeCommandInContainer(Supplier<Container.ExecResult> command) {
        Container.ExecResult execResult = command.get();
        if (!execResult.getStderr().isEmpty()) {
            throw new IllegalStateException("Failed to execute command with message: " + execResult.getStderr());
        }
    }
}
