#!/bin/bash

# CODE-IDE Setup Script
# Downloads and installs SDK, build tools, JDK, and other components

set -eu

Color_Off='\033[0m'
Red='\033[0;31m'
Green='\033[0;32m'
Blue='\033[0;34m'
Orange='\033[0;38;5;208m'

yes='^[Yy][Ee]?[Ss]?$'

# Default values
install_dir="${HOME}"
manifest="https://raw.githubusercontent.com/Neo-Mods1/CODE-IDE-resources/main/manifest.json"
assume_yes=false
sdk_version="36"
jdk_version="17"
ndk_version=""
with_git=false
with_openssh=false

print_info() {
    printf "${Blue}$1${Color_Off}\n"
}

print_err() {
    printf "${Red}$1${Color_Off}\n"
}

print_warn() {
    printf "${Orange}$1${Color_Off}\n"
}

print_success() {
    printf "${Green}$1${Color_Off}\n"
}

is_yes() {
    msg=$1
    printf "%s ([y]es/[n]o): " "$msg"
    if [ "$assume_yes" = "true" ]; then
        ans="y"
        echo "$ans"
    else
        read -r ans
    fi
    if [[ "$ans" =~ $yes ]]; then
        return 0
    fi
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

print_help() {
    echo "CODE-IDE build tools installer"
    echo ""
    echo "Usage:"
    echo "  ${0} --install-dir \$HOME --sdk 36 --jdk 17 --assume-yes"
    echo ""
    echo "Options:"
    echo "  -i, --install-dir    Installation directory (default: \$HOME)"
    echo "  -s, --sdk            Android SDK platform version (default: 36)"
    echo "  -j, --jdk            OpenJDK version: 17 or 21 (default: 17)"
    echo "  -n, --ndk            Android NDK version (e.g. 27.0.12077973, or empty to skip)"
    echo "  -m, --manifest       Manifest URL"
    echo "  -g, --with-git       Install git"
    echo "  -o, --with-openssh   Install openssh"
    echo "  -y, --assume-yes     Assume yes to all prompts"
    echo "  -h, --help           Show this help"
}

download_and_extract() {
    name=$1
    url=$2
    dir=$3
    dest=$4

    if [ ! -d "$dir" ]; then
        mkdir -p "$dir"
    fi

    cd "$dir"

    do_download=true
    if [ -f "$dest" ]; then
        name=$(basename "$dest")
        print_info "File ${name} already exists."
        if is_yes "Do you want to skip the download?"; then
            do_download=false
        fi
        echo ""
    fi

    if [ "$do_download" = "true" ]; then
        print_info "Downloading $name..."
        curl -L -o "$dest" "$url" --http1.1
        print_success "$name has been downloaded."
        echo ""
    fi

    if [ ! -f "$dest" ]; then
        print_err "The downloaded file $name does not exist. Cannot proceed..."
        exit 1
    fi

    # Detect format and extract
    print_info "Extracting downloaded archive..."
    if [[ "$dest" == *.tar.xz ]] || [[ "$dest" == *.txz ]]; then
        tar xJf "$dest"
    elif [[ "$dest" == *.tar.gz ]] || [[ "$dest" == *.tgz ]]; then
        tar xzf "$dest"
    elif [[ "$dest" == *.zip ]]; then
        unzip -o "$dest"
    else
        print_err "Unknown archive format: $dest"
        exit 1
    fi

    print_success "Extracted successfully"
    echo ""

    # Delete the downloaded file
    rm -f "$dest"
    cd - > /dev/null
}

download_resource() {
    local name=$1
    local tag=$2
    local jq_query=$3
    local dest_dir=$4

    print_info "Looking up URL for $name in manifest..."
    local url
    url=$(jq -r "$jq_query" "$downloaded_manifest")

    if [ -z "$url" ] || [ "$url" = "null" ]; then
        print_err "Could not find URL for $name in manifest"
        return 1
    fi

    print_success "Found URL: $url"
    echo ""

    download_and_extract "$name" "$url" "$dest_dir" "$dest_dir/$tag.tar.xz"
}

# Parse arguments
while [ $# -gt 0 ]; do
    case $1 in
    -i | --install-dir)
        shift
        check_arg_value "--install-dir" "${1:-}"
        install_dir="$1"
        ;;
    -s | --sdk)
        shift
        check_arg_value "--sdk" "${1:-}"
        sdk_version="$1"
        ;;
    -j | --jdk)
        shift
        check_arg_value "--jdk" "${1:-}"
        jdk_version="$1"
        ;;
    -n | --ndk)
        shift
        ndk_version="${1:-}"
        ;;
    -m | --manifest)
        shift
        check_arg_value "--manifest" "${1:-}"
        manifest="$1"
        ;;
    -g | --with-git)
        shift
        with_git=true
        ;;
    -o | --with-openssh)
        shift
        with_openssh=true
        ;;
    -y | --assume-yes)
        shift
        assume_yes=true
        ;;
    -h | --help)
        print_help
        exit 0
        ;;
    -*)
        echo "Invalid option: $1" >&2
        exit 1
        ;;
    *) break ;;
    esac
    shift
done

if [ "$jdk_version" != "17" ] && [ "$jdk_version" != "21" ]; then
    print_err "Invalid JDK version '$jdk_version'. Must be '17' or '21'."
    exit 1
fi

echo "------------------------------------------"
echo "Installation directory : ${install_dir}"
echo "SDK platform version   : ${sdk_version}"
echo "JDK version            : ${jdk_version}"
if [ -n "$ndk_version" ]; then
    echo "NDK version            : ${ndk_version}"
else
    echo "NDK                    : Skip"
fi
echo "With git               : ${with_git}"
echo "With openssh           : ${with_openssh}"
echo "Manifest URL           : ${manifest}"
echo "------------------------------------------"

if [ ! -d "$install_dir" ]; then
    print_info "Creating installation directory..."
    mkdir -p "$install_dir"
fi

# Download manifest
print_info "Downloading manifest file..."
downloaded_manifest="$install_dir/.manifest.json"
curl -L -o "$downloaded_manifest" "$manifest" && print_success "Manifest downloaded"
echo ""

# Download and extract each resource
# 1. Command-line tools
download_resource "Command-line Tools" "cmdline-tools" \
    '.resources[] | select(.tag == "cmdline-tools") | .url' \
    "$install_dir/cmdline-tools-extract"

# Move to correct location
if [ -d "$install_dir/cmdline-tools-extract" ]; then
    mkdir -p "$install_dir/cmdline-tools"
    mv "$install_dir/cmdline-tools-extract" "$install_dir/cmdline-tools/latest"
    print_success "Command-line tools installed"
fi

# 2. Platform tools
download_resource "Platform Tools" "platform-tools" \
    '.resources[] | select(.tag == "platform-tools") | .url' \
    "$install_dir"

# 3. Build tools
download_resource "Build Tools" "build-tools" \
    ".resources[] | select(.category == \"build_tools\") | select(.version | startswith(\"${sdk_version}\")) | .url" \
    "$install_dir/build-tools/${sdk_version}"

# 4. SDK Platform
download_resource "SDK Platform android-${sdk_version}" "platform" \
    ".resources[] | select(.category == \"platforms\") | select(.version == \"${sdk_version}\") | .url" \
    "$install_dir/platforms/android-${sdk_version}"

# 5. JDK
print_info "Installing OpenJDK ${jdk_version}..."
download_resource "OpenJDK ${jdk_version}" "jdk" \
    ".resources[] | select(.tag == \"openjdk-${jdk_version}\") | .url" \
    "$install_dir/opt/openjdk-${jdk_version}"

# Set JAVA_HOME
jdk_dir="$install_dir/opt/openjdk-${jdk_version}"
if [ -d "$jdk_dir" ]; then
    export JAVA_HOME="$jdk_dir"
    print_success "JAVA_HOME=$jdk_dir"
fi

# 6. SDK Licenses
download_resource "SDK Licenses" "licenses" \
    '.resources[] | select(.tag == "sdk-licenses") | .url' \
    "$install_dir/licenses-extract"

if [ -d "$install_dir/licenses-extract/licenses" ]; then
    mkdir -p "$install_dir/licenses"
    cp -r "$install_dir/licenses-extract/licenses/"* "$install_dir/licenses/"
    rm -rf "$install_dir/licenses-extract"
    print_success "SDK Licenses installed"
fi

# 7. NDK (optional)
if [ -n "$ndk_version" ]; then
    print_info "Installing Android NDK r${ndk_version}..."
    download_resource "Android NDK r${ndk_version}" "ndk" \
        ".resources[] | select(.tag == \"ndk-r${ndk_version%%.*}\") | .url" \
        "$install_dir/ndk/${ndk_version}"
else
    print_warn "Skipping NDK installation"
fi

# Install optional packages
if [ "$with_git" = "true" ] && command -v pkg &>/dev/null; then
    print_info "Installing git..."
    if [ "$assume_yes" = "true" ]; then
        pkg install git -y 2>/dev/null || print_warn "Failed to install git"
    else
        pkg install git 2>/dev/null || print_warn "Failed to install git"
    fi
fi

if [ "$with_openssh" = "true" ] && command -v pkg &>/dev/null; then
    print_info "Installing openssh..."
    if [ "$assume_yes" = "true" ]; then
        pkg install openssh -y 2>/dev/null || print_warn "Failed to install openssh"
    else
        pkg install openssh 2>/dev/null || print_warn "Failed to install openssh"
    fi
fi

# Clean up manifest
rm -f "$downloaded_manifest"

print_success ""
print_success "Setup complete!"
print_success "JAVA_HOME=$jdk_dir"
print_success "SDK installed to: $install_dir"
echo ""
print_info "You are ready to go!"
