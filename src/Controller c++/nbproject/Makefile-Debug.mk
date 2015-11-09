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
	${OBJECTDIR}/src/JNITest.o


# C Compiler Flags
CFLAGS=-m64 -shared -m64

# CC Compiler Flags
CCFLAGS=-m64 -shared -m64
CXXFLAGS=-m64 -shared -m64

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=--64

# Link Libraries and Options
LDLIBSOPTIONS=-L../../../../../../../Program\ Files/mingw-w64/x86_64-5.2.0-posix-seh-rt_v4-rev0/mingw64/lib

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ../../Controller/JNITest.dll

../../Controller/JNITest.dll: ${OBJECTFILES}
	${MKDIR} -p ../../Controller
	${LINK.cc} -o ../../Controller/JNITest.dll ${OBJECTFILES} ${LDLIBSOPTIONS} -shared

${OBJECTDIR}/src/JNITest.o: src/JNITest.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I../../../../../../../Program\ Files/Java/jdk1.8.0_65/include -I../../../../../../../Program\ Files/Java/jdk1.8.0_65/include/win32 -I../../../../../../../Program\ Files/mingw-w64/x86_64-5.2.0-posix-seh-rt_v4-rev0/mingw64/include -std=c++11 -shared -m64  -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/JNITest.o src/JNITest.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ../../Controller/JNITest.dll

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
