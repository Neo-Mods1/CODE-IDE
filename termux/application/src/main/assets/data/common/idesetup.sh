#!/bin/bash

set -eu

Color_Off='\033[0m'
Red='\033[0;31m'
Green='\033[0;32m'
Blue='\033[0;34m'
Orange="\e[38;5;208m"

yes='^[Yy][Ee]?[Ss]?$'

# Default values
arch=$(uname -m)
install_dir=$HOME
sdk_version="34"
with_cmdline=true
assume_yes=false
manifest_url="https://raw.githubusercontent.com/Neo-Mods1/CODE-IDE-resources/main/manifest.json"
pkgm="pkg"
pkg_curl="libcurl"
pkgs="jq tar"
jdk_version="17"
gradle_version="8.14.3"

print_info() {
  printf "${Blue}$1$Color_Off\n"
}

print_err() {
  printf "${Red}$1$Color_Off\n"
}

print_warn() {
  printf "${Orange}$1$Color_Off\n"
}

print_success() {
  printf "${Green}$1$Color_Off\n"
}

is_yes() {
  msg=$1
  printf "%s ([y]es/[n]o): " "$msg"
  if [ "$assume_yes" == "true" ]; then
    ans="y"
    echo "$ans"
  else
    read -r ans
  fi
  [[ "$ans" =~ $yes ]] && return 0
  return 1
}

check_arg_value() {
  option_name="$1"
  arg_value="$2"
  if [[ -z "$arg_value" ]]; then
    print_err "No value provided for $option_name!" >&2
    exit 1
  fi
}

check_command_exists() {
  command -v "$1" &>/dev/null || { print_err "Command '$1' not found!"; exit 1; }
}

install_packages() {
  if [ "$assume_yes" == "true" ]; then
    $pkgm install "$@" -y
  else
    $pkgm install "$@"
  fi
}

print_help() {
  echo "CODE-IDE build tools installer"
  echo ""
  echo "Usage:"
  echo "  ${0} -s 34 -j 17"
  echo ""
  echo "Options:"
  echo "  -i   Installation directory (default: \$HOME)"
  echo "  -s   Android SDK API level (default: 34)"
  echo "  -c   Include command-line tools"
  echo "  -j   OpenJDK version: 11, 17, 21 (default: 17)"
  echo "  -g   Install git"
  echo "  -o   Install openssh"
  echo "  -m   Manifest URL"
  echo "  -y   Assume yes (non-interactive)"
  echo "  -h   Show this help"
}

# Parse arguments
while [ $# -gt 0 ]; do
  case $1 in
    -c|--with-cmdline-tools) shift; with_cmdline=false ;;
    -g|--with-git) shift; pkgs+=" git" ;;
    -o|--with-openssh) shift; pkgs+=" openssh" ;;
    -y|--assume-yes) shift; assume_yes=true ;;
    -i|--install-dir) shift; check_arg_value "--install-dir" "${1:-}"; install_dir="$1" ;;
    -m|--manifest) shift; check_arg_value "--manifest" "${1:-}"; manifest_url="$1" ;;
    -s|--sdk) shift; check_arg_value "--sdk" "${1:-}"; sdk_version="$1" ;;
    -j|--jdk) shift; check_arg_value "--jdk" "${1:-}"; jdk_version="$1" ;;
    -h|--help) print_help; exit 0 ;;
    -*) echo "Invalid option: $1" >&2; exit 1 ;;
    *) break ;;
  esac
  shift
done

if [ "$arch" = "armv7l" ] || [ "$arch" = "armv8l" ]; then
  arch="arm"
fi

check_command_exists "$pkgm"

echo "=========================================="
echo "  CODE-IDE Environment Setup"
echo "=========================================="
echo "  Install directory : ${install_dir}"
echo "  SDK API level     : ${sdk_version}"
echo "  JDK version       : ${jdk_version}"
echo "  Command-line tools: ${with_cmdline}"
echo "  Extra packages    : ${pkgs}"
echo "  Architecture      : ${arch}"
echo "=========================================="

if ! is_yes "Confirm configuration"; then
  print_err "Aborting..."
  exit 1
fi

# Create install directory
[ ! -d "$install_dir" ] && mkdir -p "$install_dir"

# Update and install packages
print_info "Updating packages..."
$pkgm update
$pkgm upgrade ${assume_yes:+-y}

print_info "Installing required packages..."
install_packages $pkgs && print_success "Packages installed"

# Download manifest
print_info "Downloading manifest..."
manifest_file="$install_dir/manifest.json"
curl -L -o "$manifest_file" "$manifest_url" && print_success "Manifest downloaded"

# Helper: find resource URL by category (and optional version filter)
get_resource_url() {
  local category="$1"
  local filter="$2"
  jq -r --arg cat "$category" --arg filt "$filter" \
    '.resources[] | select(.category == $cat and (.name | test($filt))) | .url' \
    "$manifest_file" | head -1
}

# Download and extract a resource
download_and_extract() {
  local name="$1"
  local url="$2"
  local dest_dir="$3"
  local archive_name=$(basename "$url")

  [ -z "$url" ] && { print_err "No URL found for $name"; return 1; }

  mkdir -p "$dest_dir"
  local archive_path="$dest_dir/$archive_name"

  if [ -f "$archive_path" ]; then
    print_info "$archive_name already exists, skipping download"
  else
    print_info "Downloading $name..."
    curl -L -o "$archive_path" "$url"
    print_success "Downloaded $name"
  fi

  print_info "Extracting $name..."
  tar xJf "$archive_path" -C "$dest_dir" && print_success "Extracted $name"
  rm -f "$archive_path"
}

# ── Install Android SDK Platform ──────────────────────────────────────
print_info "Installing Android SDK Platform API ${sdk_version}..."
platform_url=$(get_resource_url "platforms" "platform-${sdk_version}")
download_and_extract "SDK Platform API ${sdk_version}" "$platform_url" "$install_dir/android-sdk/platforms/android-${sdk_version}"

# ── Install Build Tools ──────────────────────────────────────────────
print_info "Installing Build Tools..."
# Get latest build tools for the selected SDK version
bt_url=$(get_resource_url "build-tools" "build-tools-3")
download_and_extract "Build Tools" "$bt_url" "$install_dir/android-sdk/build-tools"

# ── Install Platform Tools ───────────────────────────────────────────
print_info "Installing Platform Tools..."
pt_url=$(get_resource_url "platform-tools" "platform-tools")
download_and_extract "Platform Tools" "$pt_url" "$install_dir/android-sdk/platform-tools"

# ── Install Command-line Tools ───────────────────────────────────────
if [ "$with_cmdline" = true ]; then
  print_info "Installing Command-line Tools..."
  clt_url=$(get_resource_url "sdk-tools" "cmdline-tools")
  download_and_extract "Command-line Tools" "$clt_url" "$install_dir/android-sdk/cmdline-tools"
fi

# ── Install JDK ──────────────────────────────────────────────────────
print_info "Installing OpenJDK ${jdk_version}..."
jdk_url=$(get_resource_url "jdk" "openjdk-${jdk_version}")
if [ -n "$jdk_url" ]; then
  download_and_extract "OpenJDK ${jdk_version}" "$jdk_url" "$install_dir/jdk"
  # Find the extracted jdk directory
  jdk_dir=$(find "$install_dir/jdk" -maxdepth 1 -name "jdk-*" -type d | head -1)
  if [ -n "$jdk_dir" ]; then
    export JAVA_HOME="$jdk_dir"
    print_success "JAVA_HOME=$jdk_dir"
  fi
else
  # Fallback to pkg if available
  install_packages "openjdk-${jdk_version}" || print_warn "Could not install JDK via package manager"
fi

# ── Install Gradle ──────────────────────────────────────────────────
print_info "Installing Gradle ${gradle_version}..."
gradle_url=$(get_resource_url "gradle" "gradle-${gradle_version}")
if [ -n "$gradle_url" ]; then
  download_and_extract "Gradle ${gradle_version}" "$gradle_url" "$install_dir/gradle"
  gradle_dir=$(find "$install_dir/gradle" -maxdepth 1 -name "gradle-*" -type d | head -1)
  if [ -n "$gradle_dir" ]; then
    export GRADLE_HOME="$gradle_dir"
    export PATH="$gradle_dir/bin:$PATH"
    print_success "GRADLE_HOME=$gradle_dir"
  fi
fi

# ── Write environment properties ─────────────────────────────────────
print_info "Writing environment properties..."
props_dir="$SYSROOT/etc"
props="$props_dir/ide-environment.properties"

mkdir -p "$props_dir"

cat > "$props" << EOF
JAVA_HOME=${JAVA_HOME:-/opt/openjdk}
ANDROID_SDK_ROOT=$install_dir/android-sdk
GRADLE_HOME=${GRADLE_HOME:-}
EOF

print_success "Environment properties written to $props"

rm -f "$manifest_file"
print_success "Setup complete! You are ready to build."
