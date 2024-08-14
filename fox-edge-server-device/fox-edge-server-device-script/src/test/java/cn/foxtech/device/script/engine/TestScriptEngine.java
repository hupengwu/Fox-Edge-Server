/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.device.script.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TestScriptEngine {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            engine.eval("function greetEx(name) { return greet(name) + '1'; }");
            engine.eval("function greet(name) { return 'Hello, ' + name + '!'; }");

            String name = "Alice";
            String greeting1 = (String) engine.eval("greetEx('" + name + "');");

            for (int k = 0; k < 100000000; k++) {
                String greeting = "";
                for (int i = 0; i < 100000; i++) {
                    engine.eval("function greet(name) { return 'Hello, ' + name + '!'; }");
                    greeting = (String) engine.eval("greet('" + name + "');");

                }
                System.out.println(greeting + ":" + k);
            }


        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}

