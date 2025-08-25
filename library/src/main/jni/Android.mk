# PanoramaGL library
# Version 0.2 beta
# Copyright (c) 2010 Javier Baez <javbaezga@gmail.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := glues
LOCAL_SRC_FILES := com_panoramagl_opengl_GLUES.c
LOCAL_LDLIBS    := -llog -lGLESv1_CM
LOCAL_CFLAGS	:= -fopenmp
LOCAL_LDFLAGS   += -Wl,-z,max-page-size=16384

include $(BUILD_SHARED_LIBRARY)