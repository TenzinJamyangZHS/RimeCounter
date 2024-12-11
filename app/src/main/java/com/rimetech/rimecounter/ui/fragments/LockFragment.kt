@file:Suppress("DEPRECATION")

package com.rimetech.rimecounter.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.databinding.FragmentLockBinding
import com.rimetech.rimecounter.utils.LOCKED_LIST
import com.rimetech.rimecounter.utils.POS_TOP
import com.rimetech.rimecounter.utils.getStatusBarHeight
import com.rimetech.rimecounter.utils.setMargin
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel

@Suppress("DEPRECATION")
class LockFragment : ListFragment() {
    private val lockBinding by lazy { FragmentLockBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listCounterViewModel =
            ViewModelProvider(requireActivity())[ListCounterViewModel::class.java]
        settingsViewModel = (requireActivity().application as RimeCounter).settingsViewModel
        lockBinding.lifecycleOwner = viewLifecycleOwner
        lockBinding.listCounterViewModel = listCounterViewModel
        lockBinding.settingsViewModel = settingsViewModel
        setLayoutManager(lockBinding.recyclerview)
        setTopDecoration(lockBinding.recyclerview)
        setListListener(lockBinding.recyclerview)
        setMargin()
        return lockBinding.root
    }

    override fun onResume() {
        super.onResume()
        clearPassWordInput()
        checkPassword()
    }

    override fun onPause() {
        super.onPause()
        checkPassword()
        clearPassWordInput()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setMargin()
    }

    private fun setMargin(){
        lockBinding.passwordLayout.setMargin(getStatusBarHeight(requireActivity()), POS_TOP)
    }
    private fun checkPassword() {
        lockBinding.recyclerview.visibility = View.GONE
        lockBinding.emptyText.visibility = View.GONE
        lockBinding.passwordLayout.visibility = View.VISIBLE
        lockBinding.lockScrollView.visibility=View.VISIBLE
        val savedPassword = getPassword(requireActivity())
        if (savedPassword == null) {
            setSetPasswordSetUI()
        } else {
            setCheckPasswordUI()
        }
        lockBinding.passwordResetButton.setOnClickListener { setResetPasswordUI() }

        settingsViewModel.originalPassword.observe(viewLifecycleOwner) { original ->
            settingsViewModel.lockedPassword.observe(viewLifecycleOwner) { password ->
                settingsViewModel.confirmPassword.observe(viewLifecycleOwner) { confirm ->
                    lockBinding.passwordButton.setOnClickListener {
                        if (savedPassword == null) {
                            if (password != confirm) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Password not same!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (password.isNullOrEmpty()) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Password cannot be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "Password set successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setCheckPasswordUI()
                                savePassword(requireActivity(), password)
                                onResume()
                            }
                        } else {
                            if (password != savedPassword) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Password wrong!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                lockBinding.passwordLayout.visibility=View.GONE
                                lockBinding.lockScrollView.visibility=View.GONE
                                setAdapter(
                                    lockBinding.recyclerview,
                                    LOCKED_LIST,
                                    lockBinding.emptyText
                                )
                            }
                        }
                    }

                    lockBinding.passwordSaveButton.setOnClickListener {
                        if (original != savedPassword) {
                            Toast.makeText(
                                requireActivity(),
                                "Original password wrong!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (password != confirm) {
                            Toast.makeText(
                                requireActivity(),
                                "Password not same!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (password.isNullOrEmpty()) {
                            Toast.makeText(
                                requireActivity(),
                                "Password cannot be empty!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "Password reset successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            setCheckPasswordUI()
                            savePassword(requireActivity(), password)
                            onResume()
                        }
                    }
                }
            }
        }
    }


    private fun clearPassWordInput() {
        settingsViewModel.setLockedPassword("")
        settingsViewModel.setConfirmPassword("")
        settingsViewModel.setOriginalPassword("")
    }

    private fun setSetPasswordSetUI() {
        clearPassWordInput()
        lockBinding.passwordButton.text = getString(R.string.set_password)
        lockBinding.passwordConfirmEdit.visibility = View.VISIBLE
        lockBinding.passwordEdit.hint = "Set your password"
        lockBinding.passwordConfirmEdit.hint = "Retype your password"
        lockBinding.passwordResetButton.visibility = View.GONE
        lockBinding.passwordSaveButton.visibility = View.GONE
        lockBinding.passwordOldEdit.visibility = View.GONE
        lockBinding.passwordEdit.visibility = View.VISIBLE
        lockBinding.passwordButton.visibility = View.VISIBLE
    }

    private fun setCheckPasswordUI() {
        clearPassWordInput()
        lockBinding.passwordOldEdit.visibility = View.GONE
        lockBinding.passwordEdit.visibility = View.VISIBLE
        lockBinding.passwordConfirmEdit.visibility = View.GONE
        lockBinding.passwordResetButton.visibility = View.VISIBLE
        lockBinding.passwordSaveButton.visibility = View.GONE
        lockBinding.passwordButton.text = getString(R.string.check_password)
        lockBinding.passwordButton.visibility=View.VISIBLE
        lockBinding.passwordEdit.hint = "Check your password"
    }

    private fun setResetPasswordUI() {
        clearPassWordInput()
        lockBinding.passwordButton.visibility = View.GONE
        lockBinding.passwordConfirmEdit.visibility = View.VISIBLE
        lockBinding.passwordEdit.hint = "Reset your password"
        lockBinding.passwordConfirmEdit.hint = "Retype your reset password"
        lockBinding.passwordResetButton.visibility = View.GONE
        lockBinding.passwordSaveButton.visibility = View.VISIBLE
        lockBinding.passwordOldEdit.visibility = View.VISIBLE
        lockBinding.passwordOldEdit.hint = "Type your original password"
        lockBinding.passwordEdit.visibility = View.VISIBLE
    }

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "encrypted_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun savePassword(context: Context, password: String) {
        val encryptedPrefs = getEncryptedSharedPreferences(context)
        encryptedPrefs.edit().putString("user_password", password).apply()
    }

    private fun getPassword(context: Context): String? {
        val encryptedPrefs = getEncryptedSharedPreferences(context)
        return encryptedPrefs.getString("user_password", null)
    }

}