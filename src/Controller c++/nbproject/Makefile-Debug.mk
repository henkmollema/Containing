#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=MinGW_5.2.0-Windows
CND_DLIB_EXT=dll
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/PathFinder.o \
	${OBJECTDIR}/src/node.o \
	${OBJECTDIR}/src/road_map.o \
	${OBJECTDIR}/src/vector2.o


# C Compiler Flags
CFLAGS=-m64 -Wl,--add-stdcall-alias -shared

# CC Compiler Flags
CCFLAGS=-m64 -shared
CXXFLAGS=-m64 -shared

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=--64

# Link Libraries and Options
LDLIBSOPTIONS=-L\"../../../../../../../Program\ Files/mingw-w64/x86_64-5.2.0-posix-seh-rt_v4-rev0/mingw64/lib\"

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ../Controller/build/classes/lib/Controller.dll

../Controller/build/classes/lib/Controller.dll: ${OBJECTFILES}
	${MKDIR} -p ../Controller/build/classes/lib
	${LINK.cc} -o ../Controller/build/classes/lib/Controller.dll ${OBJECTFILES} ${LDLIBSOPTIONS} -shared

${OBJECTDIR}/src/PathFinder.o: src/PathFinder.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux -I/C/Program\ Files/Java/jdk1.8.0_66/include -I/C/Program\ Files/Java/jdk1.8.0_66/include/win32 -I/C/Program\ Files/mingw-w64/x86_64-5.2.0-posix-seh-rt_v4-rev0/mingw64/include -std=c++11 -shared  -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/PathFinder.o src/PathFinder.cpp

${OBJECTDIR}/src/node.o: src/node.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux -I/C/Program\ Files/Java/jdk1.8.0_66/include -I/C/Program\ Files/Java/jdk1.8.0_66/include/win32 -I/C/Program\ Files/mingw-w64/x86_64-5.2.0-posix-seh-rt_v4-rev0/mingw64/include -std=c++11 -shared  -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/node.o src/node.cpp

${OBJECTDIR}/src/road_map.o: src/road_map.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux -I/C/Program\ Files/Java/jdk1.8.0_66/include -I/C/Program\ Files/Java/jdk1.8.0_66/include/win32 -I/C/Program\ Files/mingw-w64/x86_64-5.2.0-posix-seh-rt_v4-rev0/mingw64/include -std=c++11 -shared  -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/road_map.o src/road_map.cpp

${OBJECTDIR}/src/vector2.o: src/vector2.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux -I/C/Program\ Files/Java/jdk1.8.0_66/include -I/C/Program\ Files/Java/jdk1.8.0_66/include/win32 -I/C/Program\ Files/mingw-w64/x86_64-5.2.0-posix-seh-rt_v4-rev0/mingw64/include -std=c++11 -shared  -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/vector2.o src/vector2.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ../Controller/build/classes/lib/Controller.dll

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
