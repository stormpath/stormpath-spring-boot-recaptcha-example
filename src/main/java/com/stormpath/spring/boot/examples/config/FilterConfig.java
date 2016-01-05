/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.spring.boot.examples.config;

import com.stormpath.spring.boot.examples.filter.ReCaptchaFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean registration(ReCaptchaFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.addUrlPatterns("/login");
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filterRegistrationBean;
    }

    @Bean
    ReCaptchaFilter filter() {
        return new ReCaptchaFilter();
    }
}
