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
 
package cn.foxtech.common.utils.scheduler.multitask;

public class PeriodTaskType {
    /**
     * 周期任务：在公共线程，循环执行
     */
    public static int task_type_share = 1;

    /**
     * 周期任务：在独立线程中，循环执行
     */
    public static int task_type_alone = 2;

    /**
     * 临时性任务：执行一次
     */
    public static int task_type_once = 3;
}
