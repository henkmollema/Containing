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
	${OBJECTDIR}/src/JNI.o


# C Compiler Flags
CFLAGS=-shared -m64

# CC Compiler Flags
CCFLAGS=-shared -m64
CXXFLAGS=-shared -m64

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ../../Controller/JNI.dll

../../Controller/JNI.dll: ${OBJECTFILES}
	${MKDIR} -p ../../Controller
	${LINK.cc} -o ../../Controller/JNI.dll ${OBJECTFILES} ${LDLIBSOPTIONS} -shared

${OBJECTDIR}/src/JNI.o: src/JNI.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I../../../../../../../Program\ Files/Java/jdk1.8.0_60/include -I../../../../../../../Program\ Files/Java/jdk1.8.0_60/include/win32 -shared -m64  -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/JNI.o src/JNI.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ../../Controller/JNI.dll

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
