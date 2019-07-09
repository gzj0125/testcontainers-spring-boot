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
package com.testcontainers.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.testcontainers.boot.common.spring.DependsOnPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.testcontainers.dynamodb.DynamoDBProperties.BEAN_NAME_EMBEDDED_DYNAMODB;

@Configuration
@AutoConfigureOrder
@ConditionalOnClass(AmazonDynamoDB.class)
@ConditionalOnProperty(name = "embedded.dynamodb.enabled", matchIfMissing = true)
public class EmbeddedDynamoDBDependenciesAutoConfiguration {

    @Configuration
    public static class EmbeddedMariaDbDataSourceDependencyContext {
        @Bean
        public BeanFactoryPostProcessor dynamodbDependencyPostProcessor() {
            return new DependsOnPostProcessor(AmazonDynamoDB.class, new String[]{BEAN_NAME_EMBEDDED_DYNAMODB});
        }
    }

}
