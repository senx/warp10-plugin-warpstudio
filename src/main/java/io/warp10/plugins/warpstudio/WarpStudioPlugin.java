/*
 *  Copyright 2020 SenX S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.warp10.plugins.warpstudio;

import io.warp10.warp.sdk.AbstractWarp10Plugin;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * The type WarpStudio plugin.
 */
public class WarpStudioPlugin extends AbstractWarp10Plugin implements Runnable {

  private static final String WARPSTUDIO_MAIN_CLASS = "io.warp10.warpstudio.Main";
  private static final String CONF_WARPSTUDIO_HOST = "warpstudio.host";
  private static final String CONF_WARPSTUDIO_PORT = "warpstudio.port";

  private Properties properties = null;

  /**
   * Init.
   *
   * @param properties the properties
   */
  @Override
  public void init(Properties properties) {
    String host = properties.getProperty(CONF_WARPSTUDIO_HOST);
    String port = properties.getProperty(CONF_WARPSTUDIO_PORT);

    this.properties = properties;

    Thread t = new Thread(this);
    t.setDaemon(true);
    t.setName("[WarpStudioPlugin " + host + ":" + port + "]");
    t.start();
  }

  /**
   * Run.
   */
  @Override
  public void run() {

    try {
      Class<?> cls = this.getClass().getClassLoader().loadClass(WARPSTUDIO_MAIN_CLASS);
      Object main = cls.newInstance();
      Method meth = cls.getMethod("init", Properties.class);
      meth.invoke(main, this.properties);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
