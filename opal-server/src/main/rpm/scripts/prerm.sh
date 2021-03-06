#!/bin/sh
# prerm script for opal
#
# see: dh_installdeb(1)

set -e

# summary of how this script can be called:
#        * <prerm> `remove'
#        * <old-prerm> `upgrade' <new-version>
#        * <new-prerm> `failed-upgrade' <old-version>
#        * <conflictor's-prerm> `remove' `in-favour' <package> <new-version>
#        * <deconfigured's-prerm> `deconfigure' `in-favour'
#          <package-being-installed> <version> `removing'
#          <conflicting-package> <version>
# for details, see http://www.debian.org/doc/debian-policy/ or
# the debian-policy package

NAME=opal

stopOpalServer() {
  if which service >/dev/null 2>&1; then
    service opal stop
  elif which invoke-rc.d >/dev/null 2>&1; then
    invoke-rc.d opal stop
  else
    /etc/init.d/opal stop
  fi
}

if [ "$1" -eq 0 ] || [ "$1" -ge 2 ]; then
  stopOpalServer

  if [ "$1" -eq 0 ]; then
    # removing
    chkconfig --del opal
  fi

fi

exit 0
