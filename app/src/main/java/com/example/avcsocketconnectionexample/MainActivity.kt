package com.example.avcsocketconnectionexample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.avcsocketconnectionexample.databinding.ActivityMainBinding
import com.example.avcsocketconnectionexample.socket.ConnectionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding by lazy {
        _binding!!
    }

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        observeViewModel()
    }

    private fun initViews() {
        binding.btnGetChats.setOnClickListener {
            viewModel.getChatList()
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            repeatOn(Lifecycle.State.STARTED) {
                socketIsActive.collectLatest {
                    updateConnectionStatus(it)
                }
            }

            repeatOn(Lifecycle.State.STARTED) {
                chatsTitle.collectLatest {
                    updateChatsTitle(it)
                }
            }
        }
    }

    private fun updateConnectionStatus(isConnected: Boolean) {
        binding.tvConnectionStatus.text = getString(
            R.string.app_socket_connection_status,
            ConnectionStatus.getStatus(isConnected).label
        )
    }

    private fun updateChatsTitle(value: String) {
        binding.tvChatTitles.text = value
    }
}

private fun LifecycleOwner.repeatOn(
    state: Lifecycle.State,
    coroutineBlock: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state, coroutineBlock)
    }
}