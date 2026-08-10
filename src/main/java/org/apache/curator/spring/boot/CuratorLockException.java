/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.curator.spring.boot;

/**
 * Runtime exception raised when a distributed lock operation fails.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class CuratorLockException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new lock exception with the given detail message.
	 * @param e the detail message
	 */
	public CuratorLockException(String e) {
		super(e);
	}

	/**
	 * Creates a new lock exception wrapping the given cause.
	 * @param e the cause of this exception
	 */
	public CuratorLockException(Exception e) {
		super(e);
	}
}
