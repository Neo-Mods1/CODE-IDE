/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.setup

enum class IdeSetupArgument(val argumentName: String, val requiresValue: Boolean = false) {
    INSTALL_DIR("--install-dir", true),
    SDK_VERSION("--sdk", true),
    JDK_VERSION("--jdk", true),
    MANIFEST("--manifest", true),
    WITH_GIT("--with-git"),
    WITH_OPENSSH("--with-openssh"),
    ASSUME_YES("--assume-yes");

    companion object {
        fun buildArgs(vararg pairs: Pair<IdeSetupArgument, Any?>): Array<String> {
            val args = mutableListOf<String>()
            for ((arg, value) in pairs) {
                args.add(arg.argumentName)
                if (arg.requiresValue) {
                    args.add(value?.toString() ?: "")
                }
            }
            return args.toTypedArray()
        }
    }
}
